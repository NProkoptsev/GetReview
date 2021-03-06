package com.getreviews.repository;

import com.getreviews.domain.Category;
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

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Item> rowMapper = new RowMapper<Item>() {

        @Override
        public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            item.setRating(rs.getDouble("rating"));
            Category category = new Category();
            category.setId(rs.getLong("category_id"));
            item.setCategory(category);
            item.setCreatedDate(rs.getTimestamp("created"));
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
            Category category = new Category();
            category.setId(rs.getLong("category_id"));
            item.setCategory(category);
            item.setCreatedDate(rs.getTimestamp("created"));
            return item;
        }
    };

    @Override
    public <S extends Item> S save(S entity) {
        String sql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long id = entity.getId();

        if (id == null) {
            sql =
                "insert into item (name, description, category_id, created) values (?, ?, ?, 'NOW')";
        } else {
            sql =
                "update item set name = ?, description = ?, category_id = ? where id = ?";
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
                    if (entity.getCategory() != null)
                        ps.setLong(3, entity.getCategory().getId());
                    else
                        ps.setNull(3, java.sql.Types.BIGINT);
                    if (id != null) ps.setLong(4, entity.getId());
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
        String sql ="select it.id as id, name, description, rating, category_id, created, im.url as im_url " +
            "from item it LEFT OUTER JOIN image im on im.item_id = it.id WHERE im.id " +
            "in (SELECT image.id FROM image where image.item_id = it.id limit 1) or im.id is null " +
            "order by %s limit ? offset ?";
        String sortedSql = String.format(sql, pageable.getSort() != null ?  pageable.getSort().toString().replace(":","") : "id asc");
        List<Item> items = jdbcTemplate.query(sortedSql, fullRowMapper,
            pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
        Page<Item> page = new PageImpl<Item>(items, pageable, count());
        return page;
    }

    @Override
    public Item findOne(Long aLong) {
        Item item = jdbcTemplate.queryForObject("select id, name, description, rating, category_id, created from item WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return item;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Item> findAll() {
        List<Item> items = jdbcTemplate.query("select id, name, description, rating, category_id, created from item", rowMapper);
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
    public List<Item> findAllByExample(Item example) {
        if (example == null) {
            return null;
        }


        PreparedStatementHelper psh = new PreparedStatementHelper(
            "select id, name, description, rating, category_id, created from item WHERE");
        psh.put("id", example.getId());
        psh.put("name", example.getName());
        psh.put("description", example.getDescription());

        if (psh.statementCreator() == null) {
            return null;
        }

        List<Item> items = jdbcTemplate
            .query(psh.statementCreator(), rowMapper);
        return items;
    }


    /**
     * Find the source object by the given example.
     *
     * @param example
     * @return
     */
    @Override
    public Item findOneByExample(Item example) {
        List<Item> src = findAllByExample(example);

        if (src.size() == 0) {
            return null;
        } else {
            return src.get(0);
        }
    }

    @Override
    public List<Item> findAllLike(Item example) {
        if (example == null|| example.getName() == null
                || example.getName().isEmpty()) {
            return null;
        }

        String sql = "select id, name, description, rating, category_id, created "
                    + "from item WHERE name LIKE ?";

        PreparedStatementCreator psCreator =
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(
                        Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(
                            sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, "%" + example
                                .getName().replaceAll("'", "\"") + "%");
                        return ps;
                    }
                };

        List<Item> items = jdbcTemplate
                .query(psCreator, rowMapper);
        return items;
    }

    @Override
    public List<Item> getFourRandomItems() {
        List<Item> items = jdbcTemplate.query("select it.id as id, name, description, rating, category_id, created, im.url as im_url " +
            "from item it JOIN image im on im.item_id = it.id " +
            "WHERE im.id in (SELECT image.id FROM image where image.item_id = it.id limit 1) and it.rating > 3 " +
            "order by random() limit 8", fullRowMapper);
        return items;
    }

    @Override
    public Page<Item> findByText(Pageable pageable, String text) {
        String sql = "select it.id as id, name, description, rating, category_id, created, im.url as im_url " +
        "from item it LEFT OUTER JOIN image im on im.item_id = it.id WHERE (im.id " +
            "in (SELECT image.id FROM image where image.item_id = it.id limit 1) or im.id is null) " +
            "and fts @@ to_tsquery('russian', ?) order by %s limit ? offset ?";
        String sortedSql = String.format(sql, pageable.getSort() != null ?  pageable.getSort().toString().replace(":","") : "id asc");
        List<Item> items = jdbcTemplate.query(sortedSql,
            new Object[]{String.join("&", text.split(" +")) + ":ab",
                pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize()},
            fullRowMapper);

        Long count = jdbcTemplate.queryForObject("select count(*) from item WHERE fts @@ to_tsquery('russian', ?)",
            new Object[]{String.join("&", text.split(" +")) + ":ab"}, Long.class);
        Page<Item> page = new PageImpl<Item>(items, pageable, count);
        return page;
    }

    @Override
    public Page<Item> findAllByCategory(Pageable pageable, Long category) {
        System.out.println( pageable.getSort().toString());
        String sql = "select it.id as id, name, description, rating, category_id, created, im.url as im_url " +
            "from item it LEFT OUTER JOIN image im on im.item_id = it.id WHERE (im.id " +
            "in (SELECT image.id FROM image where image.item_id = it.id limit 1) or im.id is null) " +
            "and category_id in (select c.id from category c join category cc " +
            "on c.parent_id = cc.id where cc.id = ? " +
            "union select ? from category) " +
            "order by %s limit ? offset ?";
        String sortedSql = String.format(sql, pageable.getSort() != null ?  pageable.getSort().toString().replace(":","") : "id asc");
        List<Item> items = jdbcTemplate.query(sortedSql, fullRowMapper,
            category, category, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
        Page<Item> page = new PageImpl<Item>(items, pageable, count());
        return page;

    }
}
