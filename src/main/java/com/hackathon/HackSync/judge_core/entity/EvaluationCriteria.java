package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "evaluation_criteria")
public class EvaluationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathons_id")
    private Hackathons hackathonId;

    @Column(name = "criteria_name")
    private String criteriaName;

    private String description;

    @Column(name = "max_score")
    private int maxScore;

    private double weight;
}
