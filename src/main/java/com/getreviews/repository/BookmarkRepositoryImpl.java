package com.getreviews.repository;

import com.getreviews.domain.*;
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
public class BookmarkRepositoryImpl implements BookmarkRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Bookmark> rowMapper = new RowMapper<Bookmark>() {

        @Override
        public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bookmark bookmark = new Bookmark();
            bookmark.setId(rs.getLong("id"));
            bookmark.setDate(Instant.ofEpochMilli(rs.getDate("date").getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            Client client = new Client();
            client.setId(rs.getLong("client_id"));
            bookmark.setClient(client);
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            bookmark.setItem(item);
            return bookmark;
        }
    };

    @Override
    public <S extends Bookmark> S save(S entity) {
        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into bookmark (date, client_id, item_id) values (?, ?, ?)";
        } else {
            sql =
                "update bookmark set date = ?, client_id = ?, item_id = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setDate(1, Date.valueOf(entity.getDate()));
                    ps.setLong(2, entity.getClient().getId());
                    ps.setLong(3, entity.getItem().getId());
                    if (id != null) ps.setLong(4, entity.getId());
                    return ps;
                }
            };

        jdbcTemplate.update(psCreator, keyHolder);

        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Bookmark> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Bookmark findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Bookmark> findAll() {
        List<Bookmark> bookmarks = jdbcTemplate.query(
            "select id, date, client_id, item_id from bookmark", rowMapper);
        return bookmarks;
    }

    @Override
    public Iterable<Bookmark> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from bookmark where id = ?", aLong);
    }

    @Override
    public void delete(Bookmark entity) {

    }

    @Override
    public void delete(Iterable<? extends Bookmark> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
