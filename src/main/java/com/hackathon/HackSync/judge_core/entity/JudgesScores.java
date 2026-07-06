package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"judgeId", "projectId", "criteriaId"})
@Table(name = "judges_scores")
public class JudgesScores extends BaseClass {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Users judgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectSubmissions projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id", nullable = false)
    private EvaluationCriteria criteriaId;

    @Column(name = "score_given")
    private double scoreGiven;

    @Column(name = "feedback_notes", columnDefinition = "TEXT")
    private String feedBackNotes;

}
