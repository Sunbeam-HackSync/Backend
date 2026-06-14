package com.hackathon.HackSync.admin_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hackathons_reviews")
@AttributeOverride(name = "id", column = @Column(name = "hackathon_review_id"))
@ToString(callSuper = true, exclude = { "hackathonId", "adminId" })
public class HackathonReviews extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Users adminId;

    @Column(name = "review_status")
    @Enumerated(EnumType.STRING)
    private HackathonReviewStatus reviewStatus;

    @Column(name = "feedback_notes", columnDefinition = "TEXT")
    private String feedbackNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
