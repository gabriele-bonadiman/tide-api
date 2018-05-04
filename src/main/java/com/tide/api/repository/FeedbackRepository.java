package com.tide.api.repository;

import com.tide.api.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


/**
 * Spring Data JPA repository for the Feedback entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID>, JpaSpecificationExecutor<Feedback> {

    @Query(value =
        "SELECT " +
        " SUM(CASE WHEN jhi_like = 1 THEN 1 ELSE 0 END) AS jhi_like," +
        " SUM(CASE WHEN jhi_like = 1 THEN 0 ELSE 1 END) AS jhi_dislike" +
        " FROM Feedback " +
        " WHERE announcement_id = :announcementId")
    List<Object[]> getStat(@Param("announcementId") UUID announcementId);

}
