package com.athlas.filebrowser.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
@Setter
@Getter
public class FileService
{
    @Value("${app.files.location}")
    private String FILE_FOLDER_PATH;

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
        return new File(FILE_FOLDER_PATH + filename);
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
}

