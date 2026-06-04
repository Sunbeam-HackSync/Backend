package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_members")
public class TeamMembers {

    @Column(name = "teams_id")
    private Teams teamsId;

    @Column(name = "user_id")
    private Users userId;

    @Column(name = "is_team_leader")
    private boolean isTeamLeader = false;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
