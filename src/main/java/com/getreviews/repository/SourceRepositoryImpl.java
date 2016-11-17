package com.getreviews.repository;

import com.getreviews.domain.Item;
import com.getreviews.domain.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SourceRepositoryImpl implements SourceRepository {

    private RowMapper<Source> rowMapper = new RowMapper<Source>() {
        @Override
        public Source mapRow(ResultSet rs, int rowNum) throws SQLException {
            Source source = new Source();
            source.setId(rs.getLong("id"));
            source.setUrl(rs.getString("url"));
            source.setName(rs.getString("name"));
            source.setDescription(rs.getString("description"));
            return source;
        }
    };
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Source> S save(S entity) {
        int result = jdbcTemplate.update(
            "insert into source (url, name, description) values (?, ?, ?)",
            entity.getUrl(), entity.getName(), entity.getDescription());
        entity.setId((long) result);
        return entity;
    }

    @Override
    public <S extends Source> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Source findOne(Long aLong) {
        Source source = jdbcTemplate.queryForObject("select id, url, name, description from source WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return source;
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
