package com.getreviews.repository;

import com.getreviews.domain.Category;
import com.getreviews.domain.Item;
import com.getreviews.domain.Review;
import com.getreviews.domain.Sale;
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
 * Created by grigorijpogorelov on 28.11.16.
 */
public class SaleRepositoryImpl implements SaleRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Sale> rowMapper = new RowMapper<Sale>() {

        @Override
        public Sale mapRow(ResultSet rs, int rowNum) throws SQLException {
            Sale sale = new Sale();
            sale.setId(rs.getLong("id"));
            sale.setStart_time(Instant.ofEpochMilli(rs.getDate("start_time").getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            sale.setStart_time(Instant.ofEpochMilli(rs.getDate("end_time").getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            sale.setDescription(rs.getString("description"));
            return sale;
        }
    };

    @Override
    public <S extends Sale> S save(S entity) {
        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into sale (start_time, end_time, description) values (?, ?, ?)";
        } else {
            sql =
                "update sale set start_time = ?, end_time = ?, description = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setDate(1, Date.valueOf(entity.getStart_time()));
                    ps.setDate(2, Date.valueOf(entity.getEnd_time()));
                    ps.setString(3, entity.getDescription());
                    if (id != null) ps.setLong(4, entity.getId());
                    return ps;
                }
            };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Sale> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Sale findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Sale> findAll() {
        List<Sale> sales = jdbcTemplate.query(
            "select id, start_time, end_time from sale", rowMapper);
        return sales;
    }

    @Override
    public Iterable<Sale> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from sale where id = ?", aLong);
    }

    @Override
    public void delete(Sale entity) {

    }

    @Override
    public void delete(Iterable<? extends Sale> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
