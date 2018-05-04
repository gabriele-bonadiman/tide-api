package com.tide.api.service;

import com.tide.api.domain.Stat;
import com.tide.api.service.dto.FeedbackDTO;

import java.util.UUID;

/**
 * Service Interface for managing Feedback.
 */
public interface FeedbackService {

    /**
     * Save a feedback.
     *
     * @param feedbackDTO the entity to save
     * @return the persisted entity
     */
    FeedbackDTO save(FeedbackDTO feedbackDTO);


    /**
     * Get feedback stats
     *
     * @param announcementId the id of the announcement
     * @return Stat
     */
    Stat getFeedback(UUID announcementId);

}
