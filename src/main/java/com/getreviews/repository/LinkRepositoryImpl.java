package com.getreviews.repository;

import com.getreviews.domain.Link;
import com.getreviews.domain.Item;
import com.getreviews.domain.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

/**
 * Created by vansickle on 29/11/16.
 */
public class LinkRepositoryImpl implements LinkRepository {

    private RowMapper<Link> rowMapper = new RowMapper<Link>() {
        @Override
        public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
            Link link = new Link();
            link.setId(rs.getLong("id"));
            link.setUrl(rs.getString("url"));
            link.setDescription(rs.getString("description"));
            Item item = new Item();
            item.setId(rs.getLong("item_id"));
            link.setItem(item);
            return link;
        }
    };

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Link> S save(S entity) {

        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into link (url, item_id, description) values (?, ?, ?)";
        } else {
            sql =
                "update link set url = ?, item_id = ?, description = ? where id = ?";
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
    public <S extends Link> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Link findOne(Long aLong) {
        Link link = jdbcTemplate.queryForObject("select id, url, item_id, description from link WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return link;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Link> findAll() {
        List<Link> links = jdbcTemplate.query("select id, url, item_id, description from link", rowMapper);
        return links;
    }

    public List<Link> findByItemId(Long itemId){
        List<Link> links = jdbcTemplate.query("select id, url, item_id, description from link where link.item_id = ?",
            new Object[] {itemId},
            rowMapper);
        return links;
    }

    @Override
    public Iterable<Link> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from link", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from link where id = ?", aLong);
    }

    @Override
    public void delete(Link entity) {
        jdbcTemplate.update("delete from link where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Link> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from link");
    }
}
