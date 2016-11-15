package com.getreviews.repository;

import com.getreviews.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by grigorijpogorelov on 15.11.16.
 */

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Client> rowMapper = new RowMapper<Client>() {
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
            Client client = new Client();
            client.setId(rs.getLong("id"));
            client.setFullname(rs.getString("fullname"));
            client.setNickname(rs.getString("nickname"));
            client.setExt_or_int(rs.getBoolean("ext_or_int"));
            return client;
        }
    };

    @Override
    public <S extends Client> S save(S entity) {
        int result = jdbcTemplate.update(
            "insert into client (fullname, nickname, ext_or_int) values (?, ?, ?)",
            entity.getFullname(), entity.getNickname(), entity.isExt_or_int());
        entity.setId((long) result);
        return entity;
    }

    @Override
    public <S extends Client> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Client findOne(Long aLong) {
        Client client = jdbcTemplate.queryForObject("select id, fullname, nickname, ext_or_int from client WHERE id=?",
            new Object[]{aLong}, rowMapper);
        return client;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Client> findAll() {
        List<Client> clients= jdbcTemplate.query("select id, fullname, nickname, ext_or_int from client", rowMapper);
        return clients;
    }

    @Override
    public Iterable<Client> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {
        this.jdbcTemplate.update("delete from client where id = ?", aLong);
    }

    @Override
    public void delete(Client entity) {
        this.jdbcTemplate.update("delete from client where id = ?", entity.getId());
    }

    @Override
    public void delete(Iterable<? extends Client> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
