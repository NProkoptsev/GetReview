package com.getreviews.repository;

import com.getreviews.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
public interface ReviewRepository extends JpaRepository<Review,Long> {

}
