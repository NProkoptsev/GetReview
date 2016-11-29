package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Acquired_at;

import com.getreviews.repository.Acquired_atRepository;
import com.getreviews.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Acquired_at.
 */
@RestController
@RequestMapping("/api")
public class Acquired_atResource {

    private final Logger log = LoggerFactory.getLogger(Acquired_atResource.class);

    @Inject
    private Acquired_atRepository acquired_atRepository;

    /**
     * POST  /acquired-ats : Create a new acquired_at.
     *
     * @param acquired_at the acquired_at to create
     * @return the ResponseEntity with status 201 (Created) and with body the new acquired_at, or with status 400 (Bad Request) if the acquired_at has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/acquired-ats",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Acquired_at> createAcquired_at(@RequestBody Acquired_at acquired_at) throws URISyntaxException {
        log.debug("REST request to save Acquired_at : {}", acquired_at);
        if (acquired_at.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("acquired_at", "idexists", "A new acquired_at cannot already have an ID")).body(null);
        }
        Acquired_at result = acquired_atRepository.save(acquired_at);
        return ResponseEntity.created(new URI("/api/acquired-ats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("acquired_at", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /acquired-ats : Updates an existing acquired_at.
     *
     * @param acquired_at the acquired_at to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated acquired_at,
     * or with status 400 (Bad Request) if the acquired_at is not valid,
     * or with status 500 (Internal Server Error) if the acquired_at couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/acquired-ats",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Acquired_at> updateAcquired_at(@RequestBody Acquired_at acquired_at) throws URISyntaxException {
        log.debug("REST request to update Acquired_at : {}", acquired_at);
        if (acquired_at.getId() == null) {
            return createAcquired_at(acquired_at);
        }
        Acquired_at result = acquired_atRepository.save(acquired_at);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("acquired_at", acquired_at.getId().toString()))
            .body(result);
    }

    /**
     * GET  /acquired-ats : get all the acquired_ats.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of acquired_ats in body
     */
    @RequestMapping(value = "/acquired-ats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Acquired_at> getAllAcquired_ats() {
        log.debug("REST request to get all Acquired_ats");
        List<Acquired_at> acquired_ats = (List<Acquired_at>)acquired_atRepository.findAll();
        return acquired_ats;
    }

    /**
     * GET  /acquired-ats/:id : get the "id" acquired_at.
     *
     * @param id the id of the acquired_at to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the acquired_at, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/acquired-ats/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Acquired_at> getAcquired_at(@PathVariable Long id) {
        log.debug("REST request to get Acquired_at : {}", id);
        Acquired_at acquired_at = acquired_atRepository.findOne(id);
        return Optional.ofNullable(acquired_at)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /acquired-ats/:id : delete the "id" acquired_at.
     *
     * @param id the id of the acquired_at to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/acquired-ats/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAcquired_at(@PathVariable Long id) {
        log.debug("REST request to delete Acquired_at : {}", id);
        acquired_atRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("acquired_at", id.toString())).build();
    }

}
