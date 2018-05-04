package com.tide.api.web.rest;

import com.tide.api.TideApp;

import com.tide.api.domain.Feedback;
import com.tide.api.repository.FeedbackRepository;
import com.tide.api.service.FeedbackService;
import com.tide.api.service.dto.FeedbackDTO;
import com.tide.api.service.mapper.FeedbackMapper;
import com.tide.api.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static com.tide.api.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FeedbackResource REST controller.
 *
 * @see FeedbackResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TideApp.class)
public class FeedbackResourceIntTest {

    private static final UUID DEAFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final UUID DEFAULT_ANNOUNCEMENT_ID = UUID.randomUUID();
    private static final UUID UPDATED_ANNOUNCEMENT_ID = UUID.randomUUID();

    private static final Boolean DEFAULT_LIKE = false;
    private static final Boolean UPDATED_LIKE = true;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFeedbackMockMvc;

    private Feedback feedback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FeedbackResource feedbackResource = new FeedbackResource(feedbackService);
        this.restFeedbackMockMvc = MockMvcBuilders.standaloneSetup(feedbackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Feedback createEntity(EntityManager em) {
        Feedback feedback = new Feedback()
            .like(DEFAULT_LIKE)
            .announcementId(DEFAULT_ANNOUNCEMENT_ID);;
        return feedback;
    }

    @Before
    public void initTest() {
        feedback = createEntity(em);
    }

    @Test
    @Transactional
    public void createFeedback() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // Create the Feedback
        FeedbackDTO feedbackDTO = feedbackMapper.toDto(feedback);
        restFeedbackMockMvc.perform(post("/api/feedbacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedbackDTO)))
            .andExpect(status().isCreated());

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate + 1);
        Feedback testFeedback = feedbackList.get(feedbackList.size() - 1);
        assertThat(testFeedback.isLike()).isEqualTo(DEFAULT_LIKE);
        assertThat(testFeedback.getAnnouncementId()).isEqualTo(DEFAULT_ANNOUNCEMENT_ID);
    }

    @Test
    @Transactional
    public void createFeedbackWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // Create the Feedback with an existing ID
        feedback.setId(DEAFAULT_UUID);
        FeedbackDTO feedbackDTO = feedbackMapper.toDto(feedback);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(post("/api/feedbacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedbackDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getStatsWithValidAnnouncementID() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // Create the Feedback with an existing ID
        FeedbackDTO feedbackDTO = feedbackMapper.toDto(feedback);
        feedbackRepository.saveAndFlush(feedback);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(get("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.likes").value(0))
            .andExpect(jsonPath("$.dislikes").value(1));
        ;

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void getStatsWithInvalidAnnouncementID() throws Exception {

        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(get("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.likes").value(0))
            .andExpect(jsonPath("$.dislikes").value(0));
        ;

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate);
    }
    @Test
    @Transactional
    public void getStatsWithEmptyDatabase() throws Exception {

        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(get("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.likes").value(0))
            .andExpect(jsonPath("$.dislikes").value(0));
        ;

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void createFeedbackMethodNotAllowed() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        FeedbackDTO feedbackDTO = feedbackMapper.toDto(feedback);
        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(put("/api/feedbacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedbackDTO)))
            .andExpect(status().isMethodNotAllowed());

        restFeedbackMockMvc.perform(get("/api/feedbacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isMethodNotAllowed());

        restFeedbackMockMvc.perform(delete("/api/feedbacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getStatsMethodNotAllowed() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        FeedbackDTO feedbackDTO = feedbackMapper.toDto(feedback);
        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(put("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedbackDTO)))
            .andExpect(status().isMethodNotAllowed());

        restFeedbackMockMvc.perform(post("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedbackDTO)))
            .andExpect(status().isMethodNotAllowed());

        restFeedbackMockMvc.perform(delete("/api/feedbacks/stats/{announcementId}",DEFAULT_ANNOUNCEMENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feedback in the database
        List<Feedback> feedbackList = feedbackRepository.findAll();
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Feedback.class);
        Feedback feedback1 = new Feedback();
        feedback1.setId(DEAFAULT_UUID);
        Feedback feedback2 = new Feedback();
        feedback2.setId(feedback1.getId());
        assertThat(feedback1).isEqualTo(feedback2);
        feedback2.setId(UPDATED_UUID);
        assertThat(feedback1).isNotEqualTo(feedback2);
        feedback1.setId(null);
        assertThat(feedback1).isNotEqualTo(feedback2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeedbackDTO.class);
        FeedbackDTO feedbackDTO1 = new FeedbackDTO();
        feedbackDTO1.setId(DEAFAULT_UUID);
        FeedbackDTO feedbackDTO2 = new FeedbackDTO();
        assertThat(feedbackDTO1).isNotEqualTo(feedbackDTO2);
        feedbackDTO2.setId(feedbackDTO1.getId());
        assertThat(feedbackDTO1).isEqualTo(feedbackDTO2);
        feedbackDTO2.setId(UPDATED_UUID);
        assertThat(feedbackDTO1).isNotEqualTo(feedbackDTO2);
        feedbackDTO1.setId(null);
        assertThat(feedbackDTO1).isNotEqualTo(feedbackDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(feedbackMapper.fromId(DEAFAULT_UUID).getId()).isEqualTo(DEAFAULT_UUID);
        assertThat(feedbackMapper.fromId(null)).isNull();
    }
}
