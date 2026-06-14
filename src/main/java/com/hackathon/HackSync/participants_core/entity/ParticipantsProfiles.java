package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "userId")
@Table(name = "participants_profile")
public class ParticipantsProfiles extends BaseClass {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participants_id", nullable = false, unique = true)
    private Users userId;

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
}
