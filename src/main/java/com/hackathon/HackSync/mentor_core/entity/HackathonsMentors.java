package com.hackathon.HackSync.mentor_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hackathons_mentors")
public class HackathonsMentors {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathon_id")
    private Hackathons hackathonId;

    @Column(name = "mentor_id")
    private Users mentorsId;

    @Column(name = "expertise_tags")
    private String expertiseTags;

    @Enumerated(EnumType.STRING)
    private MentorStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
