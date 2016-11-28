package com.getreviews.repository;

import com.getreviews.domain.Category;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Category entity.
 */
@SuppressWarnings("unused")
public interface CategoryRepository extends CrudRepository<Category,Long> {


    List<Category> findAll(boolean topLevelOnly);
}
