package com.getreviews.web.rest;

import com.getreviews.GetReviewsApp;

import com.getreviews.domain.Link;
import com.getreviews.repository.LinkRepository;

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
 * Test class for the LinkResource REST controller.
 *
 * @see LinkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GetReviewsApp.class)
public class LinkResourceIntTest {

    private static final String DEFAULT_URL = "AAAAA";
    private static final String UPDATED_URL = "BBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private LinkRepository linkRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLinkMockMvc;

    private Link link;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LinkResource linkResource = new LinkResource();
        ReflectionTestUtils.setField(linkResource, "linkRepository", linkRepository);
        this.restLinkMockMvc = MockMvcBuilders.standaloneSetup(linkResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Link createEntity(EntityManager em) {
        Link link = new Link()
                .url(DEFAULT_URL)
                .description(DEFAULT_DESCRIPTION);
        return link;
    }

    @Before
    public void initTest() {
        link = createEntity(em);
    }

    @Test
    @Transactional
    public void createLink() throws Exception {
        int databaseSizeBeforeCreate = linkRepository.findAll().size();

        // Create the Link

        restLinkMockMvc.perform(post("/api/links")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(link)))
                .andExpect(status().isCreated());

        // Validate the Link in the database
        List<Link> links = linkRepository.findAll();
        assertThat(links).hasSize(databaseSizeBeforeCreate + 1);
        Link testLink = links.get(links.size() - 1);
        assertThat(testLink.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testLink.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLinks() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the links
        restLinkMockMvc.perform(get("/api/links?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(link.getId().intValue())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getLink() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get the link
        restLinkMockMvc.perform(get("/api/links/{id}", link.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(link.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLink() throws Exception {
        // Get the link
        restLinkMockMvc.perform(get("/api/links/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLink() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);
        int databaseSizeBeforeUpdate = linkRepository.findAll().size();

        // Update the link
        Link updatedLink = linkRepository.findOne(link.getId());
        updatedLink
                .url(UPDATED_URL)
                .description(UPDATED_DESCRIPTION);

        restLinkMockMvc.perform(put("/api/links")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLink)))
                .andExpect(status().isOk());

        // Validate the Link in the database
        List<Link> links = linkRepository.findAll();
        assertThat(links).hasSize(databaseSizeBeforeUpdate);
        Link testLink = links.get(links.size() - 1);
        assertThat(testLink.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testLink.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteLink() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);
        int databaseSizeBeforeDelete = linkRepository.findAll().size();

        // Get the link
        restLinkMockMvc.perform(delete("/api/links/{id}", link.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Link> links = linkRepository.findAll();
        assertThat(links).hasSize(databaseSizeBeforeDelete - 1);
    }
}
