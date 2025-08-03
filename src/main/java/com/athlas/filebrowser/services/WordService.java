package com.athlas.filebrowser.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

@Service
@AllArgsConstructor
@Setter
@Getter
public class WordService
{
    private FileService fileService;

    //private static final String PUNCTUATION_REGEX = "[~`!@#\\\\$%\\\\^&\\\\*\\\\(\\\\)-_=\\\\+\\\\[\\\\]\\\\{\\\\};:'\\\",\\\\.<>/\\\\?\\\\\\\\\\\\|]";
    private static final String PUNCTUATION_REGEX = "[~!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?\\\\|]";


    // Returns all words from folder
    public HashSet<String> getAllWords(String folderPath) throws FileNotFoundException
    {
        File[] folderContent = fileService.getFolderFiles(folderPath);

        HashSet<String> words = new HashSet<>();

        for(File file : folderContent)
        {
            HashSet<String> fileWords = getFileWords(file);

            words.addAll(fileWords);
        }

        return words;
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
            if (!sanitizedWord.isBlank())
            {
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
        }
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
}
