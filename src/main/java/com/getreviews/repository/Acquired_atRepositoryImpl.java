package com.getreviews.repository;

import com.getreviews.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class Acquired_atRepositoryImpl implements Acquired_atRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Acquired_at> rowMapper = new RowMapper<Acquired_at>() {

        @Override
        public Acquired_at mapRow(ResultSet rs, int rowNum) throws SQLException {
            Acquired_at acquired_at = new Acquired_at();
            acquired_at.setId(rs.getLong("id"));
            acquired_at.setPrice(rs.getFloat("price"));
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            acquired_at.setItem(item);
            Shop shop = new Shop();
            shop.setId(rs.getLong("shop_id"));
            acquired_at.setShop(shop);
            return acquired_at;
        }
    };

    @Override
    public <S extends Acquired_at> S save(S entity) {
        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into acquired_at (price, item_id, shop_id) values (?, ?, ?)";
        } else {
            sql =
                "update acquired_at set price = ?, item_id = ?, shop_id = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setFloat(1, entity.getPrice());
                    ps.setLong(2, entity.getItem().getId());
                    ps.setLong(3, entity.getShop().getId());
                    if (id != null) ps.setLong(4, entity.getId());
                    return ps;
                }
            };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Acquired_at> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Acquired_at findOne(Long aLong) {
        Acquired_at acquired_at = jdbcTemplate.queryForObject("select id, price, item_id, shop_id from acquired_at WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return acquired_at;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Acquired_at> findAll() {
        List<Acquired_at> acquired_ats = jdbcTemplate.query("select id, price, item_id, shop_id from acquired_at", rowMapper);
        return acquired_ats;
    }

    @Override
    public Iterable<Acquired_at> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from acquired_at", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from acquired_at where id = ?", aLong);
    }

    @Override
    public void delete(Acquired_at entity) {
        jdbcTemplate.update("delete from acquired_at where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Acquired_at> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from acquired_at");
    }
}
