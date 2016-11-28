package com.getreviews.repository;

import com.getreviews.domain.Image;
import com.getreviews.domain.Item;
import com.getreviews.domain.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ImageRepositoryImpl implements ImageRepository {

    private RowMapper<Image> rowMapper = new RowMapper<Image>() {
        @Override
        public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
            Image image = new Image();
            image.setId(rs.getLong("id"));
            image.setUrl(rs.getString("url"));
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            image.setItem(item);
            return image;
        }
    };

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Image> S save(S entity) {

        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into image (url, item_id) values (?, ?)";
        } else {
            sql =
                "update image set url = ?, item_id = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
                new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, entity.getUrl());
                ps.setLong(2, entity.getItem().getId());
                if (id != null) ps.setLong(3, entity.getId());
                return ps;
            }
        };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Image> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Image findOne(Long aLong) {
        Image image = jdbcTemplate.queryForObject("select id, url, item_id from image WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return image;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Image> findAll() {
        List<Image> images = jdbcTemplate.query("select id, url, item_id from image", rowMapper);
        return images;
    }

    @Override
    public Page<Image> findAll(Pageable pageable) {
        List<Image> items = jdbcTemplate.query("select id, url, item_id from image limit ? offset ?", rowMapper,
            pageable.getPageSize(), pageable.getPageNumber()*pageable.getPageSize());
        Page<Image> page = new PageImpl<Image>(items, pageable, count());
        return page;
    }

    @Override
    public List<Image> findByItemId(Long itemId){
        List<Image> images = jdbcTemplate.query("select id, url, item_id from image where image.item_id = ?",
            new Object[] {itemId},
            rowMapper);
        return images;
    }

    @Override
    public Iterable<Image> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from image", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from image where id = ?", aLong);
    }

    @Override
    public void delete(Image entity) {
        jdbcTemplate.update("delete from image where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Image> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from image");
    }
}
