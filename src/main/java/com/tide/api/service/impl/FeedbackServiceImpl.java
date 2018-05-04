package com.tide.api.service.impl;

import com.tide.api.domain.Feedback;
import com.tide.api.domain.Stat;
import com.tide.api.repository.FeedbackRepository;
import com.tide.api.service.FeedbackService;
import com.tide.api.service.dto.FeedbackDTO;
import com.tide.api.service.mapper.FeedbackMapper;
import com.tide.api.web.rest.errors.NullStatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Feedback.
 */
@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final Logger log = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    private final FeedbackRepository feedbackRepository;

    private final FeedbackMapper feedbackMapper;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
    }

    /**
     * Save a feedback.
     *
     * @param feedbackDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public FeedbackDTO save(FeedbackDTO feedbackDTO) {
        log.debug("Request to save Feedback : {}", feedbackDTO);
        Feedback feedback = feedbackMapper.toEntity(feedbackDTO);
        if (feedback != null){
            feedback = feedbackRepository.save(feedback);
            return feedbackMapper.toDto(feedback);
        }
        else {
            log.debug("Mapping feedbackDTO to entity return null");
            return null;
        }
    }

    /**
     * Get feedback stats
     *
     * @param announcementId the id of the announcement
     * @return Stat
     */
    @Override
    public Stat getFeedback(UUID announcementId) {
        log.debug("Request to get stats for announcement : {}", announcementId);
        List<Object[]> stats =
            feedbackRepository.getStat(announcementId);

        if (stats != null){
            Stat stat = new Stat();
            for (Object[] result : stats) {
                if (result.length > 1) {
                    stat.setLikes(result[0] != null ? (Long) result[0] : 0);
                    stat.setDislikes(result[1] != null ? (Long) result[1] : 0);
                }
                else{
                    log.debug("Result array does not contain element for announcementId: {}", announcementId);
                    stat.setLikes(0);
                    stat.setDislikes(0);
                }
            }
            return stat;
        }
        else {
            log.debug("Request to get stats for announcement : {} return null", announcementId);
            throw new NullStatException();
        }
    }

}
