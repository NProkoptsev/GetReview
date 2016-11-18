package com.getreviews.repository;

import com.getreviews.domain.Item;
import com.getreviews.domain.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
    public List<Source> findAll(Source example) {  
        if (example == null) {
            return null;
        }
        
        boolean noFieldsSpecified = true;
        StringBuilder q = new StringBuilder(
                "select id, url, name, description from source WHERE ");

        if (example.getId() != null) {
            q.append("id = " + example.getId());
            noFieldsSpecified = false;
        }
        if (example.getUrl() != null && !example.getUrl().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(", ");
            }
            q.append("url = " + example.getUrl());
            noFieldsSpecified = false;
        }
        if (example.getName() != null && !example.getName().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(", ");
            }
            q.append("name = '" + example.getName() + "'");
            noFieldsSpecified = false;
        }
        
        List<Source> src = jdbcTemplate.query(q.toString(), rowMapper);
        return src;
    }
    
    
    /**
     * Find the source object by the given example.
     * @param example
     * @return
     */
    @Override
    public Source findOne(Source example) {  
        List<Source> src = findAll(example);
        
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
}
