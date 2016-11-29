package com.getreviews.repository;

import com.getreviews.domain.Acquired_at;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Acquired_at entity.
 */
@SuppressWarnings("unused")
public interface Acquired_atRepository extends CrudRepository<Acquired_at,Long> {

}
