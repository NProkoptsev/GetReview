package com.getreviews.repository;

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

public class SourceRepositoryImpl implements SourceRepository {

    private RowMapper<Source> rowMapper = new RowMapper<Source>() {
        @Override
        public Source mapRow(ResultSet rs, int rowNum) throws SQLException {
            Source source = new Source();
            source.setId(rs.getLong("id"));
            source.setUrl(rs.getString("url"));
            source.setName(rs.getString("name"));
            return source;
        }
    };
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Source> S save(S entity) {

        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into source (url, name, description) values (?, ?, ?)";
        } else {
            sql =
                "update source set url = ?, name = ?, description = ? where id = ?";
        }

        PreparedStatementCreator psCreator =
            new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, entity.getUrl());
                    ps.setString(2, entity.getName());
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
    public <S extends Source> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Source findOne(Long aLong) {
        Source source = jdbcTemplate.queryForObject(
                "select id, url, name, description from source WHERE id=?",
                new Object[]{aLong}, rowMapper);
        return source;
    }


    /**
     * Find the source object by the given example.
     * @param example
     * @return
     */
    @Override
    public List<Source> findAllByExample(Source example) {
        if (example == null) {
            return null;
        }

        PreparedStatementHelper psh = new PreparedStatementHelper(
                "select id, url, name from source WHERE");
        psh.put("id", example.getId());
        psh.put("url", example.getUrl());
        psh.put("name", example.getName());

        if (psh.statementCreator() == null) {
            return null;
        }

        List<Source> src = jdbcTemplate.query(
                psh.statementCreator(), rowMapper);
        return src;
    }


    /**
     * Find the source object by the given example.
     * @param example
     * @return
     */
    @Override
    public Source findOneByExample(Source example) {
        List<Source> src = findAllByExample(example);

        if (src.size() == 0) {
            return null;
        } else {
            return src.get(0);
        }
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Source> findAll() {
        List<Source> sources = jdbcTemplate.query("select id, url, name, description from source", rowMapper);
        return sources;
    }

    @Override
    public Iterable<Source> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("select count(*) from source", Long.class);
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update("delete from source where id = ?", aLong);
    }

    @Override
    public void delete(Source entity) {
        jdbcTemplate.update("delete from source where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Source> entities) {

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from source");
    }

    @Override
    public Page<Source> findAll(Pageable pageable) {
        List<Source> items = jdbcTemplate.query("select id, url, name, description from source limit ? offset ?", rowMapper,
            pageable.getPageSize(), pageable.getPageNumber()*pageable.getPageSize());
        Page<Source> page = new PageImpl<Source>(items, pageable, count());
        return page;
    }
}
