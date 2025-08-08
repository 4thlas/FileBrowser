package com.athlas.filebrowser.services;

import com.athlas.filebrowser.dto.WordDTO;
import com.athlas.filebrowser.entities.FileEntity;
import com.athlas.filebrowser.entities.WordEntity;
import com.athlas.filebrowser.repositories.FileRepository;
import com.athlas.filebrowser.repositories.WordRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Setter
@Getter
@AllArgsConstructor
@Slf4j
public class DBService
{
    private final WordService wordService;
    private FileService fileService;

    private FileRepository fileRepository;
    private WordRepository wordRepository;


    @Transactional
    public void syncFilesDB() throws IOException
    {
        final File[] localFiles = fileService.getFolderFiles("files");

        List<String> localFilenames = new ArrayList<>();

        // Batch of new files
        List<FileEntity> filesToSync = new ArrayList<>();

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
                    filesToSync.add(FileEntity.builder()
                            .filename(localFileName)
                            .size(BigDecimal.valueOf(fileSize))
                            .lastModified(lastModified)
                            .checksum(fileService.calcChecksum(localFile))
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
                    String localFileChecksum = fileService.calcChecksum(localFile);

                    // Check if a file was changed
                    if (!dbFileChecksum.equals(localFileChecksum))
                    {
                        log.info("File {} changed", dbFileOptional.get().getFilename());

                        // Update checksum in DB
                        dbFileOptional.get().setChecksum(localFileChecksum);

                        filesToSync.add(dbFileOptional.get());
                    }
                }
                catch (Exception e)
                {
                    throw new IOException(e.getMessage());
                }
            }
        }

        // Save file batch if it has any files
        if (!filesToSync.isEmpty())
        {
            fileRepository.saveAll(filesToSync);
            log.info("Saved {} new files to DB", filesToSync.size());
        }

        // Get all DB file entities that don't exist locally and delete them
        List<FileEntity> orphanedFiles = fileRepository.findByFilenameNotIn(localFilenames);
        if (!orphanedFiles.isEmpty())
        {
            fileRepository.deleteAll(orphanedFiles);
            log.info("Removed {} orphaned files from DB", orphanedFiles.size());

            filesToSync.addAll(orphanedFiles);
        }

        String[] fileNamesToSync = filesToSync.stream()
                .map(FileEntity::getFilename)
                .toArray(String[]::new);

        syncWordsDB(fileNamesToSync);
    }

    public void syncWordsDB(String[] fileNamesToSync) throws IOException
    {
        if (fileNamesToSync.length == 0)
        {
            return;
        }

        log.info("Synchronizing words...");

        // Word batch
        List<WordEntity> wordsToSyncBatch = new ArrayList<>();

        List<String> localWordsStrings = new ArrayList<>();

        try
        {
            List<WordDTO> localWords = wordService.getFolderWords("files");

            for (WordDTO localWord : localWords)
            {
                localWordsStrings.add(localWord.getWord());

                // If word doesn't belong to any modified file, skip iteration
                boolean wordFileChanged = localWord.getFilenames()
                        .stream()
                        .anyMatch(filename -> List.of(fileNamesToSync).contains(filename));

                if (!wordFileChanged)
                {
                    continue;
                }

                // Prepare a list of FileEntities that contain localWord
                List<FileEntity> localWordFiles = new ArrayList<>();
                for (String wordFilename : localWord.getFilenames())
                {
                    var dbWordFileOptional = fileRepository.findByFilename(wordFilename);
                    dbWordFileOptional.ifPresent(localWordFiles::add);
                }

                // If word exists in DB, update its file list only
                Optional<WordEntity> dbExistingWordOptional = wordRepository.findByWord(localWord.getWord());
                if (dbExistingWordOptional.isPresent())
                {
                    var dbExistingWord = dbExistingWordOptional.get();

                    List<FileEntity> dbWordFiles = dbExistingWord.getFiles();

                    if (!dbWordFiles.equals(localWordFiles))
                    {
                        dbExistingWord.setFiles(localWordFiles);
                        wordsToSyncBatch.add(dbExistingWord);
                    }

                    continue;
                }

                // Add new word to a batch
                wordsToSyncBatch.add(WordEntity.builder()
                        .word(localWord.getWord())
                        .files(localWordFiles)
                        .build());
            }
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }

        // Save word batch
        wordRepository.saveAll(wordsToSyncBatch);
        if (!wordsToSyncBatch.isEmpty())
        {
            log.info("Synchronized {} words with DB in total", wordsToSyncBatch.size());
        }


        // Delete all words from DB that don't exist locally
        var orphanedWords = wordRepository.findAllByWordNotIn(localWordsStrings);
        wordRepository.deleteAll(orphanedWords);

        if (!orphanedWords.isEmpty())
        {
            log.info("Removed {} orphaned words from DB", orphanedWords.size());
        }
    }
}
