package com.tide.api.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.tide.api.domain.Stat;
import com.tide.api.service.FeedbackService;
import com.tide.api.service.dto.FeedbackDTO;
import com.tide.api.web.rest.errors.BadRequestAlertException;
import com.tide.api.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * REST controller for managing Feedback.
 */
@RestController
@RequestMapping("/api")
public class FeedbackResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

    private static final String ENTITY_NAME = "feedback";

    private final FeedbackService feedbackService;

    public FeedbackResource(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * POST  /feedbacks : Create a new feedback.
     *
     * @param feedbackDTO the feedbackDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new feedbackDTO, or with status 400 (Bad Request) if the feedback has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/feedbacks")
    @Timed
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackDTO feedbackDTO) throws URISyntaxException {
        log.debug("REST request to save Feedback : {}", feedbackDTO);
        if (feedbackDTO.getId() != null) {
            throw new BadRequestAlertException("A new feedback cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FeedbackDTO result = feedbackService.save(feedbackDTO);
        return ResponseEntity.created(new URI("/api/feedbacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * GET  /feedbacks/stats/:announcementId : get the feedback for an announcement.
     *
     * @param announcementId the announcementId of the feedbackDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the feedbackDTO, or with status 404 (Not Found)
     */
    @GetMapping("/feedbacks/stats/{announcementId}")
    @ResponseBody
    @Timed
    public ResponseEntity<Stat> getStats(@PathVariable UUID announcementId) {
        log.debug("REST request to get Feedback : {}", announcementId);
        return ResponseEntity.ok().body(feedbackService.getFeedback(announcementId));
    }


}
