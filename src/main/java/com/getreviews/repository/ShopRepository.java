package com.getreviews.repository;

import com.getreviews.domain.Shop;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Shop entity.
 */
@SuppressWarnings("unused")
public interface ShopRepository extends CrudRepository<Shop,Long> {

}
