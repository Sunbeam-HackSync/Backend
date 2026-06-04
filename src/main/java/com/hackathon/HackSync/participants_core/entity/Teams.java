package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teams")
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackthonId;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "is_looking_for_members")
    private boolean isLookingForMembers = false;

    @Column(name = "skills_needed")
    private String skillsNeeded;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
