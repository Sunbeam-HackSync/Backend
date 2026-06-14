package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_members")
@Getter
@Setter
@ToString(callSuper = true, exclude = {"teamsId", "userId"})
public class TeamMembers extends BaseClass {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams teamsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(name = "is_team_leader")
    private boolean isTeamLeader = false;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
