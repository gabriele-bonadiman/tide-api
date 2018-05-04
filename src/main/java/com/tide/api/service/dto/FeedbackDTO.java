package com.tide.api.service.dto;


import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the Feedback entity.
 */
public class FeedbackDTO implements Serializable {

    private UUID id;

    @NotNull
    private UUID announcementId;

    @NotNull
    private Boolean like;

    private Date createdAt;

    private Date lastUpdate;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(UUID announcementId) {
        this.announcementId = announcementId;
    }

    public Boolean isLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FeedbackDTO feedbackDTO = (FeedbackDTO) o;
        if(feedbackDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), feedbackDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "FeedbackDTO{" +
            "id=" + id +
            ", announcementId=" + announcementId +
            ", like=" + like +
            ", createdAt=" + createdAt +
            ", lastUpdate=" + lastUpdate +
            '}';
    }
}
