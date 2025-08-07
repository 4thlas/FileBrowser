package com.athlas.filebrowser;

import com.athlas.filebrowser.services.DBService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class FileBrowserApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(FileBrowserApplication.class, args);

        var dbService = context.getBean(DBService.class);

        Scanner scanner = new Scanner(System.in);

        try
        {
            while (true)
            {
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
