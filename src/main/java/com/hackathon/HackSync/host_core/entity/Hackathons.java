package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hackathons")
public class Hackathons {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hackathon_id")
    private UUID hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Users hostId;

    @Column(name = "title")
    private String title;

    private String tagline;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "banner_image_url")
    private String bannerImageUrl;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "min_team_size")
    private Integer minTeamSize;

    @Column(name = "max_team_size")
    private Integer maxTeamSize;

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Column(name = "hackathon_start")
    private LocalDateTime hackathonStart;

    @Column(name = "hackathon_end")
    private LocalDateTime hackathonEnd;

    @Column(name = "hackathon_status")
    @Enumerated(EnumType.STRING)
    private HackathonStatus hackathonStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
