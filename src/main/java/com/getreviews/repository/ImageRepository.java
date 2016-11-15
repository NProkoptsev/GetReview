package com.getreviews.repository;

import com.getreviews.domain.Image;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
public interface ImageRepository extends CrudRepository<Image,Long> {

}
