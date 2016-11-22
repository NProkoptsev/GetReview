package com.getreviews.repository;

import com.getreviews.domain.Image;
import com.getreviews.domain.Item;
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

public class ItemRepositoryImpl implements ItemRepository {

    private RowMapper<Item> rowMapper = new RowMapper<Item>() {

        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            item.setRating(rs.getDouble("rating"));
            return item;
        }
    };
    private RowMapper<Item> fullRowMapper = new RowMapper<Item>() {
        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            Image image = new Image();
            image.setUrl(rs.getString("im_url"));
            item.addImage(image);
            item.setRating(rs.getDouble("rating"));
            return item;
        }
    };

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Item> S save(S entity) {
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
        List<Item> items = jdbcTemplate.query("select it.id as id, name, description, rating, im.url as im_url " +
                "from item it LEFT OUTER JOIN image im on im.item_id = it.id WHERE im.id " +
                "in (SELECT image.id FROM image where image.item_id = it.id limit 1) or im.id is null " +
                "limit ? offset ?", fullRowMapper,
            pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
        Page<Item> page = new PageImpl<Item>(items, pageable, count());
        return page;
    }

    @Override
    public Item findOne(Long aLong) {
        Item item = jdbcTemplate.queryForObject("select id, name, description, rating from item WHERE id=?",
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
     *
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
            "select id, name, description, rating from item WHERE ");

        if (example.getId() != null) {
            q.append("id = " + example.getId());
            noFieldsSpecified = false;
        }
        if (example.getName() != null && !example.getName().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(" AND ");
            }
            q.append("name = '" + example.getName().replaceAll("'", "\"") + "'");
            noFieldsSpecified = false;
        }
        if (example.getDescription() != null && !example.getDescription().isEmpty()) {
            if (noFieldsSpecified == false) {
                q.append(" AND ");
            }
            q.append("description = '" + example.getDescription().replaceAll("'", "\"") + "'");
            noFieldsSpecified = false;
        }

        List<Item> items = jdbcTemplate.query(q.toString(), rowMapper);
        return items;
    }


    /**
     * Find the source object by the given example.
     *
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
            "select id, name, description, rating from item WHERE ");

        if (example.getName() != null && !example.getName().isEmpty()) {
            q.append("name LIKE '%" + example.getName().replaceAll("'", "\"") + "%'");
            noFieldsSpecified = false;
        }

        List<Item> items = jdbcTemplate.query(q.toString(), rowMapper);
        return items;
    }

    @Override
    public List<Item> getFourRandomItems() {
        List<Item> items = jdbcTemplate.query("select it.id as id, name, description, rating, im.url as im_url " +
            "from item it JOIN image im on im.item_id = it.id " +
            "WHERE im.id in (SELECT image.id FROM image where image.item_id = it.id limit 1) and it.rating > 3 " +
            "order by random() limit 4", fullRowMapper);
        return items;
    }

    @Override
    public Page<Item> findByText(Pageable pageable, String text) {
        List<Item> items = jdbcTemplate.query("select it.id as id, name, description, rating, im.url as im_url " +
                "from item it LEFT OUTER JOIN image im on im.item_id = it.id WHERE (im.id " +
                "in (SELECT image.id FROM image where image.item_id = it.id limit 1) or im.id is null) " +
                "and fts @@ to_tsquery('russian', ?) limit ? offset ?",
            new Object[]{String.join("&", text.split(" +")) + ":ab", pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize()},
            fullRowMapper);

        Long count = jdbcTemplate.queryForObject("select count(*) from item WHERE fts @@ to_tsquery('russian', ?)",
            new Object[]{String.join("&", text.split(" +")) + ":ab"}, Long.class);
        Page<Item> page = new PageImpl<Item>(items, pageable, count);
        return page;
    }
}
