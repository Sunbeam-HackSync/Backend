package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonsId", "judgeUserId", "tracksId"})
@AttributeOverride(name = "id", column = @Column(name = "hackathon_judge_id"))
@Table(name = "hackathon_judges")
public class HackathonJudges extends BaseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_user_id", nullable = false)
    private Users judgeUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private HackathonTracks tracksId;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

}
