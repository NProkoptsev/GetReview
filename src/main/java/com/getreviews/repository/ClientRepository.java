package com.getreviews.repository;

import com.getreviews.domain.Client;
import com.getreviews.domain.Source;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Client entity.
 */
@SuppressWarnings("unused")
public interface ClientRepository extends CrudRepository<Client,Long> {
    
    public Client findOne(Client example);
    
    public List<Client> findAll(Client example);
}
