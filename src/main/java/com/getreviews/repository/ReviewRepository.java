package com.getreviews.repository;

import com.getreviews.domain.Review;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
public interface ReviewRepository extends CrudRepository<Review, Long> {

}
