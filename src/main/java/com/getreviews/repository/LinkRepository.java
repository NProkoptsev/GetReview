package com.getreviews.repository;

import com.getreviews.domain.Link;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Link entity.
 */
@SuppressWarnings("unused")
public interface LinkRepository extends CrudRepository<Link,Long> {

}
