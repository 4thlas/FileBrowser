package com.athlas.filebrowser.repositories;

import com.athlas.filebrowser.entities.WordEntity;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WordRepository extends CrudRepository<WordEntity, BigDecimal>
{
    Optional<WordEntity> findByWord(String word);
    boolean existsByWord(String word);
    List<WordEntity> findAllByWordNotIn(List<String> words);
}
