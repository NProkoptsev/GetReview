package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Shop;

import com.getreviews.repository.ShopRepository;
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
 * REST controller for managing Shop.
 */
@RestController
@RequestMapping("/api")
public class ShopResource {

    private final Logger log = LoggerFactory.getLogger(ShopResource.class);

    @Inject
    private ShopRepository shopRepository;

    /**
     * POST  /shops : Create a new shop.
     *
     * @param shop the shop to create
     * @return the ResponseEntity with status 201 (Created) and with body the new shop, or with status 400 (Bad Request) if the shop has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/shops",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) throws URISyntaxException {
        log.debug("REST request to save Shop : {}", shop);
        if (shop.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("shop", "idexists", "A new shop cannot already have an ID")).body(null);
        }
        Shop result = shopRepository.save(shop);
        return ResponseEntity.created(new URI("/api/shops/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("shop", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /shops : Updates an existing shop.
     *
     * @param shop the shop to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated shop,
     * or with status 400 (Bad Request) if the shop is not valid,
     * or with status 500 (Internal Server Error) if the shop couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/shops",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Shop> updateShop(@RequestBody Shop shop) throws URISyntaxException {
        log.debug("REST request to update Shop : {}", shop);
        if (shop.getId() == null) {
            return createShop(shop);
        }
        Shop result = shopRepository.save(shop);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("shop", shop.getId().toString()))
            .body(result);
    }

    /**
     * GET  /shops : get all the shops.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of shops in body
     */
    @RequestMapping(value = "/shops",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Shop> getAllShops() {
        log.debug("REST request to get all Shops");
        List<Shop> shops = (List<Shop>)shopRepository.findAll();
        return shops;
    }

    /**
     * GET  /shops/:id : get the "id" shop.
     *
     * @param id the id of the shop to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the shop, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/shops/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Shop> getShop(@PathVariable Long id) {
        log.debug("REST request to get Shop : {}", id);
        Shop shop = shopRepository.findOne(id);
        return Optional.ofNullable(shop)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /shops/:id : delete the "id" shop.
     *
     * @param id the id of the shop to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/shops/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        log.debug("REST request to delete Shop : {}", id);
        shopRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("shop", id.toString())).build();
    }

}
