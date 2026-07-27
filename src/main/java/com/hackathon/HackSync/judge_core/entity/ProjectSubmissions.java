package com.hackathon.HackSync.judge_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonId", "trackId", "teamsId"})
@Entity
@AttributeOverride(name = "id", column = @Column(name = "project_submission_id"))
@Table(name = "project_submissions")
public class ProjectSubmissions extends BaseClass {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    /* 
    No need to have the tracks of the hackthons 
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private HackathonTracks trackId;

    //1 team 1 project
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams teamsId;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "tag_line")
    private String tagLine;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "live_demo_url")
    private String liveDemoUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "submission_status")
    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus submissionStatus;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_judge_id")
    private Users assignedJudgeId;
}
