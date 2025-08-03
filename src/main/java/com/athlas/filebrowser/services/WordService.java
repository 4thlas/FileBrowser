package com.athlas.filebrowser.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
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

    private static final String PUNCTUATION_REGEX = "[ ?!;(){}\\[\\]<>\\@#$%&*\\\\\\^'\"]";

    // Returns all words from folder
    public HashSet<String> getAllWords(String folderPath) throws FileNotFoundException
    {
        File[] folderContent = fileService.getFolderFiles("files");

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
                String[] separated = sanitizedWord.split("[\\\\/+=\\-_.,:]");

                // Add to a word set
                for (String part : separated)
                {
                    if (!part.isBlank())
                        words.add(part);
                }
            }
        }
        return words;
    }

    public String sanitizeWord(String word)
    {
        StringBuilder constructedWord = new StringBuilder();
        for (int i = 0; i < word.length(); i++)
        {
            // Allow for chars only inside words
            if (!String.valueOf(word.charAt(i)).matches(PUNCTUATION_REGEX)
                    || (i > 0 && i < word.length() - 1 &&
                    !String.valueOf(word.charAt(i - 1)).matches(PUNCTUATION_REGEX) && !String.valueOf(word.charAt(i + 1)).matches(PUNCTUATION_REGEX)))
            {
                constructedWord.append(word.charAt(i));
            }
        }
        return constructedWord.toString().toLowerCase();
    }
}
