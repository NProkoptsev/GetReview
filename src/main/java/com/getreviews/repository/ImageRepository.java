package com.getreviews.repository;

import com.getreviews.domain.Image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends CrudRepository<Image,Long> {
    public Page<Image> findAll(Pageable pageable);

    List<Image> findByItemId(Long itemId);
}
