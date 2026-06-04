package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hackathon_judges")
public class HackathonJudges {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathons_id")
    //TODO add hackathons mapping
    private Hackathons hackathonsId;

    @Column(name = "judge_user_id")
    //TODO add user mapping
    private Users judgeUserId;

    @Column(name = "track_id")
    //TODO add track mapping
    private HackathonTracks tracksId;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    //id
    //hackthon_id
    //judge_user_id
    //track_id
    //status
    //assigned_at

}
