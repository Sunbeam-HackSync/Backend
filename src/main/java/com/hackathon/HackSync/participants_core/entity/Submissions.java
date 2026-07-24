package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "team")
@Table(name = "submissions")
@AttributeOverride(name = "id", column = @Column(name = "submission_id"))
public class Submissions extends BaseClass {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false, unique = true)
    private Teams team;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "github_link")
    private String githubLink;

    @Column(name = "demo_video_link")
    private String demoVideoLink;
}
