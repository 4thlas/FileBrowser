package com.athlas.filebrowser.repositories;

import com.athlas.filebrowser.entities.FileEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends CrudRepository<FileEntity, BigDecimal>
{
    Optional<FileEntity> findByFilename(String filename);
    List<FileEntity> findByFilenameNotIn(List<String> fileNames);
}
