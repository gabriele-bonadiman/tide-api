package com.tide.api.service;

import com.tide.api.TideApp;
import com.tide.api.domain.Feedback;
import com.tide.api.domain.Stat;
import com.tide.api.repository.FeedbackRepository;
import com.tide.api.service.dto.FeedbackDTO;
import com.tide.api.service.mapper.FeedbackMapper;
import com.tide.api.web.rest.errors.NullStatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TideApp.class)
@Transactional
public class FeedbackServiceUnitTest {


    @MockBean
    private FeedbackRepository feedbackRepository;

    @MockBean
    private FeedbackMapper feedbackMapper;

    @Autowired
    private FeedbackService feedbackService;

    private static final boolean DEFAULT_LIKE = true;
    private static final boolean DEFAULT_DISLIKE = false;
    private static final Long DEFAULT_LIKES = 100L;
    private static final Long DEFAULT_DISLIKES = 22L;
    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID DEFAULT_ANNOUNCEMENT_ID = UUID.randomUUID();


    private FeedbackDTO generateFeedbackDTO(){
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setAnnouncementId(DEFAULT_ANNOUNCEMENT_ID);
        feedbackDTO.setLike(DEFAULT_LIKE);
        return feedbackDTO;
    }

    private FeedbackDTO generateUpdatedFeedbackDTO(){
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setId(DEFAULT_UUID);
        feedbackDTO.setAnnouncementId(DEFAULT_ANNOUNCEMENT_ID);
        feedbackDTO.setLike(DEFAULT_LIKE);
        feedbackDTO.setCreatedAt(new Date());
        return feedbackDTO;
    }

    private Feedback generateFeedback(){
        Feedback feedback = new Feedback();
        feedback.setId(DEFAULT_UUID);
        feedback.setAnnouncementId(DEFAULT_ANNOUNCEMENT_ID);
        feedback.setLike(DEFAULT_LIKE);
        feedback.setCreatedAt(new Date());
        return feedback;
    }

    private List<Object[]> generateObjectList(){
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{DEFAULT_LIKES,DEFAULT_DISLIKES});
        return list;
    }

    private List<Object[]> generateEmptyObjectList(){
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{});
        return list;
    }

    @Test
    @Transactional
    public void saveFeedback(){
        FeedbackDTO feedbackDTO = generateFeedbackDTO();
        FeedbackDTO updatedFeedbackDTO = generateUpdatedFeedbackDTO();
        Feedback feedback = generateFeedback();

        given(feedbackMapper.toEntity(feedbackDTO)).willReturn(feedback);
        given(feedbackRepository.save(any(Feedback.class))).willReturn(feedback);
        given(feedbackMapper.toDto(feedback)).willReturn(updatedFeedbackDTO);

        feedbackDTO = feedbackService.save(feedbackDTO);

        assertThat(feedbackDTO).isNotNull();
        assertThat(feedbackDTO.getId()).isNotNull();
        assertThat(feedbackDTO.getAnnouncementId()).isEqualTo(DEFAULT_ANNOUNCEMENT_ID);
        assertThat(feedbackDTO.isLike()).isEqualTo(DEFAULT_LIKE);
    }

    @Test
    @Transactional
    public void saveFeedbackPassingNull(){
        given(feedbackMapper.toEntity(generateFeedbackDTO())).willReturn(null);
        FeedbackDTO feedbackDTO = feedbackService.save(null);

        assertThat(feedbackDTO).isNull();
    }

    @Test
    @Transactional
    public void saveFeedbackRepositoryReturnNullObject(){
        FeedbackDTO feedbackDTO = generateFeedbackDTO();
        given(feedbackMapper.toEntity(generateFeedbackDTO())).willReturn(generateFeedback());
        given(feedbackRepository.save(any(Feedback.class))).willReturn(null);

        feedbackDTO = feedbackService.save(feedbackDTO);
        assertThat(feedbackDTO).isNull();
    }

    @Test
    @Transactional
    public void saveFeedbackErrorMapping(){
        FeedbackDTO feedbackDTO = generateFeedbackDTO();
        Feedback feedback = generateFeedback();
        FeedbackDTO wrongMapping = new FeedbackDTO();

        wrongMapping.setId(UUID.randomUUID());
        wrongMapping.setAnnouncementId(UUID.randomUUID());
        wrongMapping.setLike(DEFAULT_DISLIKE);
        wrongMapping.setCreatedAt(new Date(System.currentTimeMillis()-24*60*60*1000));

        given(feedbackMapper.toEntity(feedbackDTO)).willReturn(feedback);
        given(feedbackRepository.save(any(Feedback.class))).willReturn(feedback);
        given(feedbackMapper.toDto(feedback)).willReturn(wrongMapping);

        FeedbackDTO feedbackDTOresutl = feedbackService.save(feedbackDTO);

        assertThat(feedbackDTOresutl).isNotNull();
        assertThat(feedbackDTOresutl.getId()).isNotNull();
        assertThat(feedbackDTOresutl.getId()).isNotEqualTo(feedback.getId().toString());
        assertThat(feedbackDTOresutl.getAnnouncementId()).isNotEqualTo(feedback.getAnnouncementId().toString());
        assertThat(feedbackDTOresutl.isLike()).isNotEqualTo(feedback.isLike());
        assertThat(feedbackDTOresutl.getCreatedAt()).isNotEqualTo(feedback.getCreatedAt());
    }


    @Test
    @Transactional
    public void saveFeedbackMappingReturnNull(){
        FeedbackDTO feedbackDTO = generateFeedbackDTO();
        Feedback feedback = generateFeedback();

        given(feedbackMapper.toEntity(feedbackDTO)).willReturn(feedback);
        given(feedbackRepository.save(any(Feedback.class))).willReturn(feedback);
        given(feedbackMapper.toDto(feedback)).willReturn(null);

        feedbackDTO = feedbackService.save(feedbackDTO);

        assertThat(feedbackDTO).isNull();
    }


    @Test
    @Transactional
    public void getFeedback(){
        given(feedbackRepository.getStat(any(UUID.class))).willReturn(generateObjectList());

        Stat stat = feedbackService.getFeedback(DEFAULT_ANNOUNCEMENT_ID);

        assertThat(stat).isNotNull();
        assertThat(stat.getLikes()).isEqualTo(DEFAULT_LIKES);
        assertThat(stat.getDislikes()).isEqualTo(DEFAULT_DISLIKES);
    }

    @Test(expected = NullStatException.class)
    @Transactional
    public void getFeedbackPassingNull(){
        given(feedbackRepository.getStat(any(UUID.class))).willReturn(null);
        feedbackService.getFeedback(DEFAULT_ANNOUNCEMENT_ID);
    }

    @Test
    @Transactional
    public void getFeedbackEmptyResult(){
        given(feedbackRepository.getStat(any(UUID.class))).willReturn(generateEmptyObjectList());

        Stat stat = feedbackService.getFeedback(DEFAULT_ANNOUNCEMENT_ID);

        assertThat(stat).isNotNull();
        assertThat(stat.getLikes()).isEqualTo(0);
        assertThat(stat.getDislikes()).isEqualTo(0);
    }

}
