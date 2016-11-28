package com.getreviews.repository;

import com.getreviews.domain.Client;
import com.getreviews.domain.Item;
import com.getreviews.domain.Review;
import com.getreviews.domain.Source;
import com.getreviews.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by vansickle on 31/10/16.
 */
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private final String FULL_QUERY = "select r.id, r.text, r.rating, " +
        "r.source_id, s.url, s.name as \"source_name\", s.description, " +
        "r.client_id, c.fullname, c.nickname, c.ext_or_int, " +
        "r.item_id, i.name as \"item_name\", i.description , r.created, r.updated " +
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
            review.setCreatedDate(rs.getTimestamp("created"));
            review.setUpdatedDate(rs.getTimestamp("updated"));
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
            review.setCreatedDate(rs.getTimestamp("created"));
            review.setUpdatedDate(rs.getTimestamp("updated"));
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

        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into review (text, rating, source_id, client_id, item_id)"
                    + " values (?, ?, ?, ?, ?)";
        } else {
            sql =
                "update review set text = ?, rating = ?, source_id = ?, client_id = ?, item_id = ?"
            + " where id = ?";
        }
        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, entity.getText());
                    ps.setDouble(2, entity.getRating());
                    ps.setLong(3, entity.getSource().getId());
                    if (entity.getClient() != null)
                        ps.setLong(4, entity.getClient().getId());
                    else {
                        String currentUserLogin = SecurityUtils.getCurrentUserLogin();
                        Long clientID = jdbcTemplate.queryForObject("select id from client where nickname = ?", new Object[]{currentUserLogin}, Long.class);
                        ps.setLong(4, clientID);
                    }
                    ps.setLong(5, entity.getItem().getId());
                    if (id != null) ps.setLong(6, entity.getId());
                    return ps;
                }
            };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
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
        List<Review> reviews = jdbcTemplate.query(
            "select id, text, rating, source_id, client_id, item_id, created, updated from review", partialRowMapper);
        return reviews;
    }

    @Override
    public Iterable<Review> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        List<Review> items = jdbcTemplate.query("select id, text, rating, source_id, client_id, item_id, created, updated from review limit ? offset ?", partialRowMapper,
            pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
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

    @Override
    public Review findOneByExample(Review example) {
        List<Review> rvs = findAllByExample(example);

        if (rvs.size() == 0) {
            return null;
        } else {
            return rvs.get(0);
        }
    }

    @Override
    public List<Review> findAllByExample(Review example) {
        if (example == null) {
            return null;
        }

        PreparedStatementHelper psh = new PreparedStatementHelper(
                "select id, text, rating, source_id, client_id, item_id, "
                        + "created, updated from review WHERE");
        psh.put("id", example.getId());
        psh.put("text", example.getText());
        psh.put("rating", example.getRating());
        try {
            psh.put("source_id", example.getSource().getId());
            psh.put("client_id", example.getClient().getId());
            psh.put("item_id", example.getItem().getId());
        } catch (NullPointerException e) {
            // do nothing
        }

        if (psh.statementCreator() == null) {
            return null;
        }

        List<Review> rvs = jdbcTemplate.query(
                psh.statementCreator(), partialRowMapper);
        return rvs;
    }
}
