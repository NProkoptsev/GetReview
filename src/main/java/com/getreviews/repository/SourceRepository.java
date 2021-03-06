package com.getreviews.repository;

import com.getreviews.domain.Source;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Source entity.
 */
@SuppressWarnings("unused")
public interface SourceRepository extends CrudRepository<Source,Long> {
    /**
     * Find the source object(s) by the given example.
     * @param example
     * @return
     */
    public Source findOneByExample(Source example);
    
    public List<Source> findAllByExample(Source example);

    public Page<Source> findAll(Pageable pageable);
}
