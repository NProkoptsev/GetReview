package com.getreviews.repository;

import com.getreviews.domain.Item;
import com.getreviews.domain.Source;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Item entity.
 */
@SuppressWarnings("unused")
public interface ItemRepository extends CrudRepository<Item,Long> {
    public Page<Item> findAll(Pageable pageable);

    public Item findOneByExample(Item example);

    public List<Item> findAllByExample(Item example);

    public List<Item> findAllLike(Item example);

    public Page<Item> findByText(Pageable pageable, String text);

    public List<Item> getFourRandomItems();

    public Page<Item> findAllByCategory(Pageable pageable, Long category);

    List<Long> countByCategory();
}
