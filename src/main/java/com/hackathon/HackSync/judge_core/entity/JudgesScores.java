package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "judges_scores")
public class JudgesScores {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "judge_id")
    private Users judgeId;

    @Column(name = "project_id")
    private ProjectSubmissions projectId;

    @Column(name = "criteria_id")
    private EvaluationCriteria criteriaId;

    @Column(name = "score_given")
    private double scoreGiven;

    @Column(name = "feedback_notes", columnDefinition = "TEXT")
    private String feedBackNotes;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
