package com.getreviews.repository;

import com.getreviews.domain.Acquired_at;

/**
 * Created by grigorijpogorelov on 29.11.16.
 */
public class Acquired_atRepositoryImpl implements Acquired_atRepository {
    @Override
    public <S extends Acquired_at> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Acquired_at> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Acquired_at findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Acquired_at> findAll() {
        return null;
    }

    @Override
    public Iterable<Acquired_at> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(Acquired_at entity) {

    }

    @Override
    public void delete(Iterable<? extends Acquired_at> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
