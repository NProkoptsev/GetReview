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

        final String sql = 
                "insert into item (name, description) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        PreparedStatementCreator psCreator = 
                new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getDescription());
                return ps;
            }
        };
        
        jdbcTemplate.update(psCreator, keyHolder);
        
        entity.setId((long) keyHolder.getKeys().get("id"));
        return entity;
    }

    @Override
    public <S extends Item> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Page<Item> findAll(Pageable pageable) {
        List<Item> items = jdbcTemplate.query("select id, name, description from item limit ? offset ?", rowMapper,
            pageable.getPageSize(), pageable.getPageNumber()*pageable.getPageSize());
        Page<Item> page = new PageImpl<Item>(items, pageable, count());
        return page;
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
    
    
    /**
     * Find the source object by the given example.
     * @param example
     * @return
     */
    @Override
    public List<Item> findAll(Item example) {  
        if (example == null) {
            return null;
        }
        
        boolean noFieldsSpecified = true;
        StringBuilder q = new StringBuilder(
                "select id, name, description from item WHERE ");

        if (example.getId() != null) {
            q.append("id = " + example.getId());
            noFieldsSpecified = false;
        }
        if (example.getName() != null && !example.getName().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(" AND ");
            }
            q.append("name = '" + example.getName() + "'");
            noFieldsSpecified = false;
        }
        if (example.getDescription() != null && !example.getDescription().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(" AND ");
            }
            q.append("description = '" + example.getDescription() + "'");
            noFieldsSpecified = false;
        }
        
        List<Item> items = jdbcTemplate.query(q.toString(), rowMapper);
        return items;
    }
    
    
    /**
     * Find the source object by the given example.
     * @param example
     * @return
     */
    @Override
    public Item findOne(Item example) {  
        List<Item> src = findAll(example);
        
        if (src.size() == 0) {
            return null;
        } else {
            return src.get(0);
        }
    }

    @Override
    public List<Item> findAllLike(Item example) {
        if (example == null) {
            return null;
        }
        
        boolean noFieldsSpecified = true;
        StringBuilder q = new StringBuilder(
                "select id, name, description from item WHERE ");

        if (example.getName() != null && !example.getName().isEmpty()) {
            q.append("name LIKE '%" + example.getName() + "%'");
            noFieldsSpecified = false;
        }
        
        List<Item> items = jdbcTemplate.query(q.toString(), rowMapper);
        return items;
    }
}
