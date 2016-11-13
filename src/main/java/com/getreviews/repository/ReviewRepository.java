package com.getreviews.repository;

import com.getreviews.domain.Review;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
public interface ReviewRepository extends CrudRepository<Review,Long> {

    List<Review> findByItemId(Long itemId);
}
