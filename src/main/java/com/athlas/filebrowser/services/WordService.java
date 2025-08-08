package com.athlas.filebrowser.services;

import com.athlas.filebrowser.dto.WordDTO;
import com.athlas.filebrowser.entities.FileEntity;
import com.athlas.filebrowser.entities.WordEntity;
import com.athlas.filebrowser.repositories.FileRepository;
import com.athlas.filebrowser.repositories.WordRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class WordService
{
    private final WordRepository wordRepository;
    private final FileRepository fileRepository;
    private FileService fileService;

    // Returns all words from folder
    public List<WordDTO> getFolderWords(String folderPath) throws IOException
    {
        File[] folderContent = fileService.getFolderFiles(folderPath);

        Map<String, WordDTO> wordMap = new HashMap<>();

        for(File file : folderContent)
        {
            String[] fileWords = getFileWords(file);

            for (String word : fileWords)
            {
                WordDTO wordDTO = wordMap.get(word);

                // If it's a new word, add it to the list
                if (wordDTO == null)
                {
                    wordDTO = WordDTO.builder()
                            .word(word)
                            .filenames(new ArrayList<>())
                            .build();
                    wordMap.put(word, wordDTO);
                }

                wordDTO.getFilenames().add(file.getName()); // Update wordDTO list of filenames it occurs in
            }
        }

        log.info("Extracted {} words from {} files", wordMap.size(), folderContent.length);
        return new ArrayList<>(wordMap.values());
    }

    // Returns all words that appear in the file
    public String[] getFileWords(File file) throws IOException
    {
        String rawText;

        rawText = Files.readString(file.toPath());

        return getWordsArray(rawText, false);
    }

    public String[] getWordsArray(String rawText, boolean allowDuplicates)
    {
        List<String> readyWords = new ArrayList<>();

        String[] rawWords;

        // Split by whitespaces, new lines and some chars
        rawWords = rawText.split("[\\s\\n_\\-\\+=\\\\/\\.,:]+");

        // Make lowercase and remove all special chars outside each word
        rawWords = Arrays.stream(rawWords)
                .map(String::toLowerCase)
                .map(word -> word.replaceAll("(^[^a-zA-Z0-9]+)|([^a-zA-Z0-9]+$)", ""))
                .toArray(String[]::new);

        for (String word : rawWords)
        {
            if (word.isBlank()) // Skip every blank string
            {
                continue;
            }

            // Add word to a readyWords list if allowDuplicates = true or remove duplicates otherwise
            if (allowDuplicates)
            {
                readyWords.add(word);
            }
            else
            {
                boolean duplicateFound = Arrays.asList(readyWords.toArray()).contains(word);
                if (!duplicateFound)
                {
                    readyWords.add(word);
                }
            }
        }

        return readyWords.toArray(new String[0]);
    }
}
