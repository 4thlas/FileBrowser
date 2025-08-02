package com.athlas.filebrowser.repositories;

import com.athlas.filebrowser.entities.File;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface FileRepository extends CrudRepository<File, BigDecimal>
{

}
