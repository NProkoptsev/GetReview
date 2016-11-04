package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Source;

import com.getreviews.repository.SourceRepository;
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
 * REST controller for managing Source.
 */
@RestController
@RequestMapping("/api")
public class SourceResource {

    private final Logger log = LoggerFactory.getLogger(SourceResource.class);
        
    @Inject
    private SourceRepository sourceRepository;

    /**
     * POST  /sources : Create a new source.
     *
     * @param source the source to create
     * @return the ResponseEntity with status 201 (Created) and with body the new source, or with status 400 (Bad Request) if the source has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sources",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Source> createSource(@RequestBody Source source) throws URISyntaxException {
        log.debug("REST request to save Source : {}", source);
        if (source.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("source", "idexists", "A new source cannot already have an ID")).body(null);
        }
        Source result = sourceRepository.save(source);
        return ResponseEntity.created(new URI("/api/sources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("source", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sources : Updates an existing source.
     *
     * @param source the source to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated source,
     * or with status 400 (Bad Request) if the source is not valid,
     * or with status 500 (Internal Server Error) if the source couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sources",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Source> updateSource(@RequestBody Source source) throws URISyntaxException {
        log.debug("REST request to update Source : {}", source);
        if (source.getId() == null) {
            return createSource(source);
        }
        Source result = sourceRepository.save(source);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("source", source.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sources : get all the sources.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sources in body
     */
    @RequestMapping(value = "/sources",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Source> getAllSources() {
        log.debug("REST request to get all Sources");
        List<Source> sources = sourceRepository.findAll();
        return sources;
    }

    /**
     * GET  /sources/:id : get the "id" source.
     *
     * @param id the id of the source to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the source, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sources/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Source> getSource(@PathVariable Long id) {
        log.debug("REST request to get Source : {}", id);
        Source source = sourceRepository.findOne(id);
        return Optional.ofNullable(source)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sources/:id : delete the "id" source.
     *
     * @param id the id of the source to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sources/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        log.debug("REST request to delete Source : {}", id);
        sourceRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("source", id.toString())).build();
    }

}
