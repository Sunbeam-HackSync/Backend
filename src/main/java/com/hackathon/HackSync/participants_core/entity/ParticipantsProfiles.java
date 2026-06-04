package com.hackathon.HackSync.participants_core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participants_profile")
public class ParticipantsProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    //TODO add mapping
    private User userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "github_url")
    private String githubURL;

    @Column(name = "linkedin_url")
    private String linkedInURL;

    @Column(name = "x_url")
    private String xURL;

    @Column(name = "tech_skills")
    private String techSkills;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
