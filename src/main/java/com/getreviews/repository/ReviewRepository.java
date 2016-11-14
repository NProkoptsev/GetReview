package com.getreviews.repository;

import com.getreviews.domain.Review;
<<<<<<< HEAD
=======

import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findByItemId(Long itemId);
}
