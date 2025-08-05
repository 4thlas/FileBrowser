package com.athlas.filebrowser;

import com.athlas.filebrowser.dto.WordDTO;
import com.athlas.filebrowser.services.FileService;
import com.athlas.filebrowser.services.WordService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

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
//            File file = fileService.openFile("hamis.txt");
//
//            HashSet<String> words = wordService.getFileWords(file);
//
//
            List<WordDTO> words = wordService.getFolderWords("files");

            for (WordDTO word : words)
            {
                System.out.println(word.getWord() + " " + word.getFilenames());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

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
