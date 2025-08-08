package com.athlas.filebrowser;

import com.athlas.filebrowser.services.DBService;
import com.athlas.filebrowser.services.FileService;
import com.athlas.filebrowser.services.WordService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class FileBrowserApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(FileBrowserApplication.class, args);

        var dbService = context.getBean(DBService.class);
        var fileService = context.getBean(FileService.class);
        var wordService = context.getBean(WordService.class);

        Scanner scanner = new Scanner(System.in);

        try
        {
            //File file = fileService.openFile("minos.txt");

            while (true)
            {
//                System.out.println("=============");
//                for (String word : wordService.getFileWords(file))
//                {
//                    System.out.println(word);
//                }
//                System.out.println("=============");

                dbService.syncFilesDB();
                scanner.nextLine();
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }
}
