package com.getreviews.repository;

import com.getreviews.domain.Sale;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Sale entity.
 */
@SuppressWarnings("unused")
public interface SaleRepository extends CrudRepository<Sale,Long> {

}
