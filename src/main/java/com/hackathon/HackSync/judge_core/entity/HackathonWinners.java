package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonId", "submissionId"})
@Table(name = "hackathon_winners")
@AttributeOverride(name = "id", column = @Column(name = "winner_id"))
public class HackathonWinners extends BaseClass {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_submission_id", nullable = false)
    private ProjectSubmissions submissionId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;
}
