package com.getreviews.repository;

import com.getreviews.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA repository for the Client entity.
 */
@SuppressWarnings("unused")
public interface ClientRepository extends CrudRepository<Client, Long> {
    public Page<Client> findAll(Pageable pageable);
}
