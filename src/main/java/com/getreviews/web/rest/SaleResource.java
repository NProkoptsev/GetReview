package com.getreviews.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Sale;

import com.getreviews.repository.SaleRepository;
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
 * REST controller for managing Sale.
 */
@RestController
@RequestMapping("/api")
public class SaleResource {

    private final Logger log = LoggerFactory.getLogger(SaleResource.class);

    @Inject
    private SaleRepository saleRepository;

    /**
     * POST  /sales : Create a new sale.
     *
     * @param sale the sale to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sale, or with status 400 (Bad Request) if the sale has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) throws URISyntaxException {
        log.debug("REST request to save Sale : {}", sale);
        if (sale.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sale", "idexists", "A new sale cannot already have an ID")).body(null);
        }
        Sale result = saleRepository.save(sale);
        return ResponseEntity.created(new URI("/api/sales/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sale", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sales : Updates an existing sale.
     *
     * @param sale the sale to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sale,
     * or with status 400 (Bad Request) if the sale is not valid,
     * or with status 500 (Internal Server Error) if the sale couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sale> updateSale(@RequestBody Sale sale) throws URISyntaxException {
        log.debug("REST request to update Sale : {}", sale);
        if (sale.getId() == null) {
            return createSale(sale);
        }
        Sale result = saleRepository.save(sale);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sale", sale.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sales : get all the sales.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of sales in body
     */
    @RequestMapping(value = "/sales",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Sale> getAllSales() {
        log.debug("REST request to get all Sales");
        List<Sale> sales = (List<Sale>) saleRepository.findAll();
        return sales;
    }

    /**
     * GET  /sales/:id : get the "id" sale.
     *
     * @param id the id of the sale to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sale, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sales/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sale> getSale(@PathVariable Long id) {
        log.debug("REST request to get Sale : {}", id);
        Sale sale = saleRepository.findOne(id);
        return Optional.ofNullable(sale)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sales/:id : delete the "id" sale.
     *
     * @param id the id of the sale to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sales/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        log.debug("REST request to delete Sale : {}", id);
        saleRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sale", id.toString())).build();
    }

}
