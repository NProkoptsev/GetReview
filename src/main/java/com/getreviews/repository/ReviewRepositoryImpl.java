//package com.getreviews.repository;
//
//import com.getreviews.domain.Review;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by vansickle on 31/10/16.
// */
//@Repository
//public class ReviewRepositoryImpl implements ReviewRepository {
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//    private RowMapper<Review> rowMapper = new RowMapper<Review>() {
//        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Review review = new Review();
//            review.setId(rs.getLong("id"));
//            review.setText(rs.getString("text"));
//            review.setRating(rs.getFloat("rating"));
//            return review;
//        }
//    };
//
//    @Override
//    public <S extends Review> S save(S entity) {
//
//        int result = this.jdbcTemplate.update(
//            "insert into review (text, rating, item_id) values (?, ?, ?)",
//            entity.getText(), entity.getRating(), entity.getItemId());
//
//        entity.setId((long) result);
//        return entity;
//    }
//
//    @Override
//    public <S extends Review> Iterable<S> save(Iterable<S> entities) {
//
//
//        throw new RuntimeException("EXC2");
//
////        return entities;
//    }
//
//    @Override
//    public Review findOne(Long aLong) {
//        Review review = jdbcTemplate.queryForObject("select id, text, rating, item_id " +
//                "from review WHERE id=?",
//            new Object[]{aLong} , rowMapper);
//        return review;
//    }
//
//    @Override
//    public boolean exists(Long aLong) {
//        return false;
//    }
//
//    @Override
//    public List<Review> findAll() {
//        List<Review> reviews = jdbcTemplate.query(
//            "select id, text, rating from review",
//            rowMapper);
//
//        return reviews;
//    }
//
//    @Override
//    public Iterable<Review> findAll(Iterable<Long> longs) {
//        return null;
//    }
//
//    @Override
//    public long count() {
//        return 0;
//    }
//
//    @Override
//    public void delete(Long aLong) {
//
//    }
//
//    @Override
//    public void delete(Review entity) {
//
//    }
//
//    @Override
//    public void delete(Iterable<? extends Review> entities) {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }
//}
