package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Bookmark;

import com.getreviews.repository.BookmarkRepository;
import com.getreviews.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Bookmark.
 */
@RestController
@RequestMapping("/api")
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    @Inject
    private BookmarkRepository bookmarkRepository;

    /**
     * POST  /bookmarks : Create a new bookmark.
     *
     * @param bookmark the bookmark to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookmark, or with status 400 (Bad Request) if the bookmark has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bookmarks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bookmark> createBookmark(@Valid @RequestBody Bookmark bookmark) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmark);
        if (bookmark.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bookmark", "idexists", "A new bookmark cannot already have an ID")).body(null);
        }
        Bookmark result = bookmarkRepository.save(bookmark);
        return ResponseEntity.created(new URI("/api/bookmarks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bookmark", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks : Updates an existing bookmark.
     *
     * @param bookmark the bookmark to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmark,
     * or with status 400 (Bad Request) if the bookmark is not valid,
     * or with status 500 (Internal Server Error) if the bookmark couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bookmarks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bookmark> updateBookmark(@Valid @RequestBody Bookmark bookmark) throws URISyntaxException {
        log.debug("REST request to update Bookmark : {}", bookmark);
        if (bookmark.getId() == null) {
            return createBookmark(bookmark);
        }
        Bookmark result = bookmarkRepository.save(bookmark);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bookmark", bookmark.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookmarks : get all the bookmarks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarks in body
     */
    @RequestMapping(value = "/bookmarks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Bookmark> getAllBookmarks() {
        log.debug("REST request to get all Bookmarks");
        List<Bookmark> bookmarks = (List<Bookmark>) bookmarkRepository.findAll();
        return bookmarks;
    }

    /**
     * GET  /bookmarks/:id : get the "id" bookmark.
     *
     * @param id the id of the bookmark to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookmark, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/bookmarks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Bookmark> getBookmark(@PathVariable Long id) {
        log.debug("REST request to get Bookmark : {}", id);
        Bookmark bookmark = bookmarkRepository.findOne(id);
        return Optional.ofNullable(bookmark)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bookmarks/:id : delete the "id" bookmark.
     *
     * @param id the id of the bookmark to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/bookmarks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        log.debug("REST request to delete Bookmark : {}", id);
        bookmarkRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bookmark", id.toString())).build();
    }

}
