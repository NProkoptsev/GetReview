package com.getreviews.repository;

import com.getreviews.domain.Sale;
import com.getreviews.domain.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by grigorijpogorelov on 29.11.16.
 */
public class ShopRepositoryImpl implements ShopRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Shop> rowMapper = new RowMapper<Shop>() {

        @Override
        public Shop mapRow(ResultSet rs, int rowNum) throws SQLException {
            Shop shop = new Shop();
            shop.setId(rs.getLong("id"));
            shop.setName(rs.getString("name"));
            shop.setDescription(rs.getString("description"));
            return shop;
        }
    };

    @Override
    public <S extends Shop> S save(S entity) {
        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into shop (name, description) values (?, ?)";
        } else {
            sql =
                "update shop set name = ?, description = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, entity.getName());
                    ps.setString(2, entity.getDescription());
                    if (id != null) ps.setLong(3, entity.getId());
                    return ps;
                }
            };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Shop> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Shop findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Shop> findAll() {
        List<Shop> shops = jdbcTemplate.query(
            "select id, name, description from shop", rowMapper);
        return shops;
    }

    @Override
    public Iterable<Shop> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from shop where id = ?", aLong);
    }

    @Override
    public void delete(Shop entity) {

    }

    @Override
    public void delete(Iterable<? extends Shop> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
