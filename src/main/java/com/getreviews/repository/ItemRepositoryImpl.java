package com.getreviews.repository;

import com.getreviews.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    private RowMapper<Item> rowMapper = new RowMapper<Item>() {
        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            return item;
        }
    };

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public <S extends Item> S save(S entity) {
        int result = jdbcTemplate.update(
            "insert into item (name, description) values (?, ?)",
            entity.getName(), entity.getDescription());
        entity.setId((long) result);
        return entity;
    }

    @Override
    public <S extends Item> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Item findOne(Long aLong) {
        Item item = jdbcTemplate.queryForObject("select id, name, description from item WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return item;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Item> findAll() {
        List<Item> items = jdbcTemplate.query("select id, name, description from item", rowMapper);
        return items;
    }

    @Override
    public Iterable<Item> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from item", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from item where id = ?", aLong);
    }

    @Override
    public void delete(Item entity) {
        jdbcTemplate.update("delete from item where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Item> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from item");
    }
}
