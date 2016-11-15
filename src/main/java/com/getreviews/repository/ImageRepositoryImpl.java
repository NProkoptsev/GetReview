package com.getreviews.repository;

import com.getreviews.domain.Image;
import com.getreviews.domain.Item;
import com.getreviews.domain.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        int result = jdbcTemplate.update(
            "insert into image (url, item_id) values (?, ?)",
            entity.getUrl(), entity.getItem().getId());
        entity.setId((long) result);
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
