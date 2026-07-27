package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hackathons")
@Getter
@Setter
@ToString(callSuper = true, exclude = "hostId")
@AttributeOverride(name = "id", column = @Column(name = "hackathon_id"))
public class Hackathons extends BaseClass {

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

    @Column(name = "faq", columnDefinition = "TEXT")
    private String faq;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "result_declaration_date")
    private LocalDateTime resultDeclarationDate;
}
