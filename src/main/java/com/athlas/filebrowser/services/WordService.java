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
import java.io.FileNotFoundException;
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
    public List<WordDTO> getFolderWords(String folderPath) throws FileNotFoundException
    {
        File[] folderContent = fileService.getFolderFiles(folderPath);

        Map<String, WordDTO> wordMap = new HashMap<>();

        for(File file : folderContent)
        {
            HashSet<String> fileWords = getFileWords(file);

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
    public HashSet<String> getFileWords(File file) throws FileNotFoundException
    {
        HashSet<String> words = new HashSet<>();

        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\\s+"); // Delimiter to read word by word

        while (scanner.hasNext())
        {
            String sanitizedWord = sanitizeWord(scanner.next());

            // Skip blank words
            if (sanitizedWord.isBlank())
            {
                continue;
            }

            // Separate connected words
            String[] separated = sanitizedWord.split("[_\\.,:\\+=/\\\\-]");

            // Add to a word set
            for (String part : separated)
            {
                if (!part.isBlank())
                {
                    words.add(part);
                }
            }
        }

        log.info("Extracted {} words from file {}", words.size(), file.getName());
        return words;
    }

    // Removes leading and ending punctuation marks
    public String sanitizeWord(String word)
    {
        return word
                .replaceAll("[\\p{Cf}]", "") // Remove invisible characters
                .replaceAll("^[\\p{Punct}\\p{IsPunctuation}…]+|[\\p{Punct}\\p{IsPunctuation}…]+$", "")
                .toLowerCase();
    }

    public ArrayList<String> getWordOccurrences(WordEntity word)
    {
        ArrayList<String> fileList = new ArrayList<>();

        for (FileEntity file : word.getFiles())
        {
            fileList.add(file.getFilename());
        }

        return fileList;
    }
}
