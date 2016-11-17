package com.getreviews.repository;

import com.getreviews.domain.Client;
import com.getreviews.domain.Item;
import com.getreviews.domain.Review;
import com.getreviews.domain.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by vansickle on 31/10/16.
 */
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private final String FULL_QUERY = "select r.id, r.text, r.rating, " +
        "r.source_id, s.url, s.name as \"source_name\", s.description, " +
        "r.client_id, c.fullname, c.nickname, c.ext_or_int, " +
        "r.item_id, i.name as \"item_name\", i.description " +
        "from review r " +
        "join source s on r.source_id = s.id " +
        "join client c on r.client_id = c.id " +
        "join item i on r.item_id = i.id ";
    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Review> fullRowMapper = new RowMapper<Review>() {
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId(rs.getLong("id"));
            review.setText(rs.getString("text"));
            review.setRating(rs.getFloat("rating"));
            Source source = new Source();
            source.setId(rs.getLong("source_id"));
            source.setUrl(rs.getString("url"));
            source.setName(rs.getString("source_name"));
            source.setDescription(rs.getString("description"));
            review.setSource(source);
            Client client = new Client();
            client.setId(rs.getLong("client_id"));
            client.setFullname(rs.getString("fullname"));
            client.setNickname(rs.getString("nickname"));
            client.setExt_or_int(rs.getBoolean("ext_or_int"));
            review.setClient(client);
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            item.setName(rs.getString("item_name"));
            item.setDescription(rs.getString("description"));
            review.setItem(item);
            return review;
        }
    };

    private RowMapper<Review> partialRowMapper = new RowMapper<Review>() {
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId(rs.getLong("id"));
            review.setText(rs.getString("text"));
            review.setRating(rs.getFloat("rating"));
            Source source = new Source();
            source.setId(rs.getLong("source_id"));
            review.setSource(source);
            Client client = new Client();
            client.setId(rs.getLong("client_id"));
            review.setClient(client);
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            review.setItem(item);
            return review;
        }
    };

    @Override
    public <S extends Review> S save(S entity) {

        int result = jdbcTemplate.update(
            "insert into review (text, rating, source_id, client_id, item_id) values (?, ?, ?, ?, ?)",
            entity.getText(), entity.getRating(), entity.getSource().getId(), entity.getClient().getId(), entity.getItem().getId());

        entity.setId((long) result);
        return entity;
    }

    @Override
    public <S extends Review> Iterable<S> save(Iterable<S> entities) {


        throw new RuntimeException("EXC2");

//        return entities;
    }

    @Override
    public Review findOne(Long aLong) {
        Review review = jdbcTemplate.queryForObject(FULL_QUERY +
                "WHERE r.id=?",
            new Object[]{aLong}, fullRowMapper);
        return review;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public List<Review> findAll() {
        List<Review> reviews = jdbcTemplate.query("select id, text, rating, source_id, client_id, item_id from review", partialRowMapper);
        return reviews;
    }

    @Override
    public Iterable<Review> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        List<Review> items = jdbcTemplate.query("select id, text, rating, source_id, client_id, item_id from review limit ? offset ?", partialRowMapper,
            pageable.getPageSize(), pageable.getPageNumber()*pageable.getPageSize());
        Page<Review> page = new PageImpl<Review>(items, pageable, count());
        return page;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from review", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from review where id = ?", aLong);
    }

    @Override
    public void delete(Review entity) {
        jdbcTemplate.update("delete from review where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Review> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from review");
    }

    @Override
    public List<Review> findByItemId(Long itemId) {
        List<Review> reviews = jdbcTemplate.query(
            FULL_QUERY + "where item_id = ?",
            new Object[]{itemId},
            fullRowMapper);
        return reviews;
    }
}
