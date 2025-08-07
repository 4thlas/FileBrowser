package com.athlas.filebrowser.services;

import com.athlas.filebrowser.repositories.FileRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
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
}


