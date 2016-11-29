package com.getreviews.repository;

import com.getreviews.domain.Bookmark;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Bookmark entity.
 */
@SuppressWarnings("unused")
public interface BookmarkRepository extends CrudRepository<Bookmark,Long> {

}
