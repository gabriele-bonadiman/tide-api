package com.tide.api.service.mapper;

import com.tide.api.domain.Feedback;
import com.tide.api.service.dto.FeedbackDTO;
import org.mapstruct.Mapper;

import java.util.UUID;

/**
 * Mapper for the entity Feedback and its DTO FeedbackDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FeedbackMapper extends EntityMapper<FeedbackDTO, Feedback> {



    default Feedback fromId(UUID id) {
        if (id == null) {
            return null;
        }
        Feedback feedback = new Feedback();
        feedback.setId(id);
        return feedback;
    }
}
