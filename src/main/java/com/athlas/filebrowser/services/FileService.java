package com.athlas.filebrowser.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FileService
{
    private File currentFile;

    public File openFile(String filename) throws FileNotFoundException
    {
        currentFile = new File("files/" + filename);
        return currentFile;
    }

    public void printFile() throws FileNotFoundException
    {
        Scanner scanner = new Scanner(currentFile);

        while (scanner.hasNextLine())
        {
            System.out.println(scanner.nextLine());
        }
    }

    public ArrayList<String> getWords() throws FileNotFoundException
    {
        ArrayList<String> words = new ArrayList<>();

        Scanner scanner = new Scanner(currentFile);
        scanner.useDelimiter(" +");

        while (scanner.hasNext())
        {
            words.add(scanner.next());
        }

        return words;
    }
}
