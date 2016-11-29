package com.getreviews.web.rest;

import com.getreviews.GetReviewsApp;

import com.getreviews.domain.Acquired_at;
import com.getreviews.repository.Acquired_atRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the Acquired_atResource REST controller.
 *
 * @see Acquired_atResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GetReviewsApp.class)
public class Acquired_atResourceIntTest {

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    @Inject
    private Acquired_atRepository acquired_atRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAcquired_atMockMvc;

    private Acquired_at acquired_at;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Acquired_atResource acquired_atResource = new Acquired_atResource();
        ReflectionTestUtils.setField(acquired_atResource, "acquired_atRepository", acquired_atRepository);
        this.restAcquired_atMockMvc = MockMvcBuilders.standaloneSetup(acquired_atResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Acquired_at createEntity(EntityManager em) {
        Acquired_at acquired_at = new Acquired_at()
                .price(DEFAULT_PRICE);
        return acquired_at;
    }

    @Before
    public void initTest() {
        acquired_at = createEntity(em);
    }

    @Test
    @Transactional
    public void createAcquired_at() throws Exception {
        int databaseSizeBeforeCreate = acquired_atRepository.findAll().size();

        // Create the Acquired_at

        restAcquired_atMockMvc.perform(post("/api/acquired-ats")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(acquired_at)))
                .andExpect(status().isCreated());

        // Validate the Acquired_at in the database
        List<Acquired_at> acquired_ats = acquired_atRepository.findAll();
        assertThat(acquired_ats).hasSize(databaseSizeBeforeCreate + 1);
        Acquired_at testAcquired_at = acquired_ats.get(acquired_ats.size() - 1);
        assertThat(testAcquired_at.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    public void getAllAcquired_ats() throws Exception {
        // Initialize the database
        acquired_atRepository.saveAndFlush(acquired_at);

        // Get all the acquired_ats
        restAcquired_atMockMvc.perform(get("/api/acquired-ats?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(acquired_at.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    public void getAcquired_at() throws Exception {
        // Initialize the database
        acquired_atRepository.saveAndFlush(acquired_at);

        // Get the acquired_at
        restAcquired_atMockMvc.perform(get("/api/acquired-ats/{id}", acquired_at.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(acquired_at.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAcquired_at() throws Exception {
        // Get the acquired_at
        restAcquired_atMockMvc.perform(get("/api/acquired-ats/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAcquired_at() throws Exception {
        // Initialize the database
        acquired_atRepository.saveAndFlush(acquired_at);
        int databaseSizeBeforeUpdate = acquired_atRepository.findAll().size();

        // Update the acquired_at
        Acquired_at updatedAcquired_at = acquired_atRepository.findOne(acquired_at.getId());
        updatedAcquired_at
                .price(UPDATED_PRICE);

        restAcquired_atMockMvc.perform(put("/api/acquired-ats")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAcquired_at)))
                .andExpect(status().isOk());

        // Validate the Acquired_at in the database
        List<Acquired_at> acquired_ats = acquired_atRepository.findAll();
        assertThat(acquired_ats).hasSize(databaseSizeBeforeUpdate);
        Acquired_at testAcquired_at = acquired_ats.get(acquired_ats.size() - 1);
        assertThat(testAcquired_at.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void deleteAcquired_at() throws Exception {
        // Initialize the database
        acquired_atRepository.saveAndFlush(acquired_at);
        int databaseSizeBeforeDelete = acquired_atRepository.findAll().size();

        // Get the acquired_at
        restAcquired_atMockMvc.perform(delete("/api/acquired-ats/{id}", acquired_at.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Acquired_at> acquired_ats = acquired_atRepository.findAll();
        assertThat(acquired_ats).hasSize(databaseSizeBeforeDelete - 1);
    }
}
