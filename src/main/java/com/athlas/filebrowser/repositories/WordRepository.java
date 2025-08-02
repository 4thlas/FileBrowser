package com.athlas.filebrowser.repositories;

import com.athlas.filebrowser.entities.Word;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface WordRepository extends CrudRepository<Word, BigDecimal> {
}
