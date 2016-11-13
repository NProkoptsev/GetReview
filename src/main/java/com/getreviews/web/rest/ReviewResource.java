package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Review;

import com.getreviews.repository.ReviewRepository;
import com.getreviews.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;

    /**
     * POST  /reviews : Create a new review.
     *
     * @param review the review to create
     * @return the ResponseEntity with status 201 (Created) and with body the new review, or with status 400 (Bad Request) if the review has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> createReview(@RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to save Review : {}", review);
        if (review.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("review", "idexists", "A new review cannot already have an ID")).body(null);
        }
        Review result = reviewRepository.save(review);
        return ResponseEntity.created(new URI("/api/reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("review", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reviews : Updates an existing review.
     *
     * @param review the review to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated review,
     * or with status 400 (Bad Request) if the review is not valid,
     * or with status 500 (Internal Server Error) if the review couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> updateReview(@RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to update Review : {}", review);
        if (review.getId() == null) {
            return createReview(review);
        }
        Review result = reviewRepository.save(review);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("review", review.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reviews : get all the reviews.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of reviews in body
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getAllReviews() {
        log.debug("REST request to get all Reviews");
        List<Review> reviews = (List<Review>) reviewRepository.findAll();
        return reviews;
    }

    /**
     * GET  /reviews/:id : get the "id" review.
     *
     * @param id the id of the review to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the review, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        Review review = reviewRepository.findOne(id);
        return Optional.ofNullable(review)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reviews/:id : delete the "id" review.
     *
     * @param id the id of the review to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("review", id.toString())).build();
    }

    @RequestMapping(value = "/items/{itemId}/reviews",
    method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed
    public List<Review> getReviewsByItem(@PathVariable Long itemId){

        return reviewRepository.findByItemId(itemId);

//        ArrayList<Review> reviews = new ArrayList<>();
//        Review review = new Review();
//        review.setId(1002L);
//        review.setText("Test");
//        reviews.add(review);

//        return reviews;
    }
}
