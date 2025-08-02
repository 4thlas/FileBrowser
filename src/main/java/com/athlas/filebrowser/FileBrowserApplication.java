package com.athlas.filebrowser;

import com.athlas.filebrowser.services.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.FileNotFoundException;

@SpringBootApplication
public class FileBrowserApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(FileBrowserApplication.class, args);

        var fileService = context.getBean(FileService.class);

        // TEST
        try
        {
            fileService.openFile("minos.txt");

            for(String word : fileService.getWords())
            {
                System.out.println(word);
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
        }
    }
}
