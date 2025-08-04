package com.athlas.filebrowser;

import com.athlas.filebrowser.services.FileService;
import com.athlas.filebrowser.services.WordService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

@SpringBootApplication
public class FileBrowserApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(FileBrowserApplication.class, args);

        var fileService = context.getBean(FileService.class);
        var wordService = context.getBean(WordService.class);

        try
        {
            fileService.syncFilesDB();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }
}
