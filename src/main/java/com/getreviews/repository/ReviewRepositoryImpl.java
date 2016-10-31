package com.getreviews.repository;

import com.getreviews.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vansickle on 31/10/16.
 */
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Review> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Review> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Review findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public List<Review> findAll() {
//        ArrayList<Review> reviews = new ArrayList<>();
//        Review review = new Review();
//        review.setId(1002L);
//        review.setText("Test");
//        reviews.add(review);

        List<Review> reviews = jdbcTemplate.query(
            "select id, text, rating from review",
            new RowMapper<Review>() {
                public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Review review = new Review();
                    review.setId(rs.getLong("id"));
                    review.setText(rs.getString("text"));
                    review.setRating(rs.getFloat("rating"));
                    return review;
                }
            });

        return reviews;
    }

    @Override
    public Iterable<Review> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(Review entity) {

    }

    @Override
    public void delete(Iterable<? extends Review> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
