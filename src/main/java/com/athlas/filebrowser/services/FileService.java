package com.athlas.filebrowser.services;

import com.athlas.filebrowser.entities.FileEntity;
import com.athlas.filebrowser.repositories.FileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@Setter
@Getter
@AllArgsConstructor
@Slf4j
public class FileService
{
    private FileRepository fileRepository;

    // Return .txt files only
    public File[] getFolderFiles(String path)
    {
        final File folder = new File(path);

        File[] folderContent = folder.listFiles();

        if (folderContent == null)
        {
            log.warn("{} does not exist or is not a directory", path);
            return new File[0];
        }

        log.info("Found {} local files", folderContent.length);

        ArrayList<File> qualifiedFiles = new ArrayList<>();

        for (final File file : folderContent)
        {
            String extension = getFileExtension(file);

            // Check if entity is a .txt file
            if (file.isFile() && extension != null && extension.equals("txt"))
            {
                qualifiedFiles.add(file);
            }
        }

        return qualifiedFiles.toArray(new File[0]);
    }

    public File openFile(String filename)
    {
        return new File("files/" + filename);
    }

    public String getFileExtension(File file)
    {
        String filename = file.getName();

        // Count periods
        int periodCount = filename.length() - filename.replace(".", "").length();

        // Return null if a file has no extension
        if (periodCount == 0)
        {
            return null;
        }

        String[] arr = filename.split("\\.");

        return arr[arr.length - 1];
    }

    public void printFile(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine())
        {
            System.out.println(scanner.nextLine());
        }
    }

    public String calcChecksum(File file) throws IOException, NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // Open file input stream
        try (InputStream inputStream = new FileInputStream(file))
        {
            byte[] buffer = new byte[8192];
            int bytesRead;

            // Feed read bytes into hash calculation
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] md5Bytes = digest.digest(); // Finalize calculation


        StringBuilder builtChecksum = new StringBuilder();

        // Convert bytes to string
        for (byte b : md5Bytes)
        {
            builtChecksum.append(String.format("%02x", b));
        }

        return builtChecksum.toString();
    }

    @Transactional
    public void syncFilesDB() throws IOException
    {
        final File[] localFiles = getFolderFiles("files");

        List<String> localFilenames = new ArrayList<>();

        // Batch of new files
        List<FileEntity> newFiles = new ArrayList<>();

        for (File localFile : localFiles)
        {
            // Get file name and add it to a local filenames list
            String localFileName = localFile.getName();
            localFilenames.add(localFileName);

            // Get file metadata
            long fileSize = localFile.length();
            Date lastModified = new Date(localFile.lastModified());

            Optional<FileEntity> dbFileOptional = fileRepository.findByFilename(localFileName);

            // Create a new file entity in DB if not found
            if (dbFileOptional.isEmpty())
            {
                try
                {
                    newFiles.add(FileEntity.builder()
                            .filename(localFileName)
                            .size(BigDecimal.valueOf(fileSize))
                            .lastModified(lastModified)
                            .checksum(calcChecksum(localFile))
                            .build());
                }
                catch (Exception e)
                {
                    throw new IOException(e.getMessage());
                }
            }
            else
            {
                try
                {
                    String dbFileChecksum = dbFileOptional.get().getChecksum();
                    String localFileChecksum = calcChecksum(localFile);

                    // Check if a file was changed
                    if (!dbFileChecksum.equals(localFileChecksum))
                    {
                        log.info("File {} changed", dbFileOptional.get().getFilename());

                        // Update checksum in DB
                        dbFileOptional.get().setChecksum(localFileChecksum);
                        fileRepository.save(dbFileOptional.get());

                        // TODO
                    }
                }
                catch (Exception e)
                {
                    throw new IOException(e.getMessage());
                }
            }
        }

        // Save file batch
        fileRepository.saveAll(newFiles);
        log.info("Saved {} new files to DB", newFiles.size());

        // Get all DB file entities that don't exist locally and delete them
        List<FileEntity> orphanedFiles = fileRepository.findByFilenameNotIn(localFilenames);
        fileRepository.deleteAll(orphanedFiles);
        log.info("Removed {} orphaned files from DB", orphanedFiles.size());
    }
}


