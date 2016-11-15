package com.getreviews.repository;

import com.getreviews.domain.Source;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Source entity.
 */
@SuppressWarnings("unused")
public interface SourceRepository extends CrudRepository<Source,Long> {

}
