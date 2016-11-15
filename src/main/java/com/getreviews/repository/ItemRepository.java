package com.getreviews.repository;

import com.getreviews.domain.Item;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Item entity.
 */
@SuppressWarnings("unused")
public interface ItemRepository extends CrudRepository<Item,Long> {

}
