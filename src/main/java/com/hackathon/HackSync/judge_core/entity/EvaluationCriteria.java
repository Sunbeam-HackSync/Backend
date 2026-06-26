package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "hackathonId")
@Entity
@Table(name = "evaluation_criteria")
@AttributeOverride(name = "id", column = @Column(name = "criteria_id"))
public class EvaluationCriteria extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @Column(name = "criteria_name")
    private String criteriaName;

    private String description;

    @Column(name = "max_score")
    private int maxScore;

    private double weight;
}
