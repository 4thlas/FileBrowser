package com.athlas.filebrowser.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
@AllArgsConstructor
@Setter
@Getter
public class FileService
{
    private static final String PUNCTUATION_REGEX = "[ ?!;(){}\\[\\]<>\\@#$%&*\\\\\\^'\"]";

    // Return .txt files only
    public File[] getFolderFiles(String path)
    {
        final File folder = new File(path);

        File[] folderContent = folder.listFiles();

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

    public File openFile(String filename) throws FileNotFoundException
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

    // List all words that appear in the file
    public HashSet<String> getWords(File file) throws FileNotFoundException
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
            // Check if char is not on regex
            if (!String.valueOf(word.charAt(i)).matches(PUNCTUATION_REGEX))
            {
                constructedWord.append(word.charAt(i));
            }
            else
            {
                // Allow for char if it's in the middle of string
                if (i > 0 && i < word.length() - 1)
                {
                    if (!String.valueOf(word.charAt(i - 1)).matches(PUNCTUATION_REGEX) && !String.valueOf(word.charAt(i + 1)).matches(PUNCTUATION_REGEX))
                    {
                        constructedWord.append(word.charAt(i));
                    }
                }
            }
        }
        return constructedWord.toString();
    }
}
