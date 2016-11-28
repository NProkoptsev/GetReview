package com.getreviews.repository;

import com.getreviews.domain.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by grigorijpogorelov on 22.11.16.
 */
public class CategoryRepositoryImpl implements CategoryRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Category> rowMapper = new RowMapper<Category>() {
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setImage(rs.getString("image"));
            category.setName(rs.getString("name"));
            category.setParent_id(rs.getLong("parent_id"));
            return category;
        }
    };

    @Override
    public <S extends Category> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Category> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Category findOne(Long aLong) {
        Category category = jdbcTemplate.queryForObject(
                "select id, name, image, parent_id from category WHERE id=?",
                    new Object[]{aLong}, rowMapper);
        return category;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Category> findAll() {
        List<Category> categories = jdbcTemplate.query(
                "select id, name, image, parent_id from category", rowMapper);
        return categories;
    }


    @Override
    public Iterable<Category> findAll(Iterable<Long> longs) {
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
    public void delete(Category entity) {

    }

    @Override
    public void delete(Iterable<? extends Category> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Category> findAll(boolean topLevelOnly) {
        String sql = "select id, name, image, parent_id from category";
        if(topLevelOnly)
            sql += " where parent_id IS NULL";
        List<Category> categories = jdbcTemplate.query(
            sql, rowMapper);
        return categories;
    }
}
