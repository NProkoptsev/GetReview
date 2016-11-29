package com.getreviews.web.rest;

import com.getreviews.GetReviewsApp;

import com.getreviews.domain.Bookmark;
import com.getreviews.domain.Client;
import com.getreviews.domain.Item;
import com.getreviews.repository.BookmarkRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BookmarkResource REST controller.
 *
 * @see BookmarkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GetReviewsApp.class)
public class BookmarkResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private BookmarkRepository bookmarkRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBookmarkMockMvc;

    private Bookmark bookmark;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BookmarkResource bookmarkResource = new BookmarkResource();
        ReflectionTestUtils.setField(bookmarkResource, "bookmarkRepository", bookmarkRepository);
        this.restBookmarkMockMvc = MockMvcBuilders.standaloneSetup(bookmarkResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bookmark createEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark()
                .date(DEFAULT_DATE);
        // Add required entity
        Client client = ClientResourceIntTest.createEntity(em);
        em.persist(client);
        em.flush();
        bookmark.setClient(client);
        // Add required entity
        Item item = ItemResourceIntTest.createEntity(em);
        em.persist(item);
        em.flush();
        bookmark.setItem(item);
        return bookmark;
    }

    @Before
    public void initTest() {
        bookmark = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmark() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Bookmark

        restBookmarkMockMvc.perform(post("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmark)))
                .andExpect(status().isCreated());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).hasSize(databaseSizeBeforeCreate + 1);
        Bookmark testBookmark = bookmarks.get(bookmarks.size() - 1);
        assertThat(testBookmark.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarks() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get all the bookmarks
        restBookmarkMockMvc.perform(get("/api/bookmarks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);

        // Get the bookmark
        restBookmarkMockMvc.perform(get("/api/bookmarks/{id}", bookmark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bookmark.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBookmark() throws Exception {
        // Get the bookmark
        restBookmarkMockMvc.perform(get("/api/bookmarks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        // Update the bookmark
        Bookmark updatedBookmark = bookmarkRepository.findOne(bookmark.getId());
        updatedBookmark
                .date(UPDATED_DATE);

        restBookmarkMockMvc.perform(put("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBookmark)))
                .andExpect(status().isOk());

        // Validate the Bookmark in the database
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarks.get(bookmarks.size() - 1);
        assertThat(testBookmark.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void deleteBookmark() throws Exception {
        // Initialize the database
        bookmarkRepository.saveAndFlush(bookmark);
        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();

        // Get the bookmark
        restBookmarkMockMvc.perform(delete("/api/bookmarks/{id}", bookmark.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
