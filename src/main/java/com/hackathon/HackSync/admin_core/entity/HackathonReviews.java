package com.hackathon.HackSync.admin_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "hackathons_reviews")
public class HackathonReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathon_id")
    //TODO hackathons mapping
    private Hackathons hackathon_id;

    @Column(name = "admin_id")
    //TODO Users mapping
    private Users admin_id;

    @Column(name = "review_status")
    @Enumerated(EnumType.STRING)
    private HackathonReviewStatus reviewStatus;

    @Column(name = "feedback_notes", columnDefinition = "TEXT")
    private String feedbackNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
