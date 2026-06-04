package com.hackathon.HackSync.mentor_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.participants_core.entity.Teams;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "help_tickets")
public class HelpTickets {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathon_id")
    private Hackathons hackathonId;

    @Column(name = "team_id")
    private Teams teamId;

    @Column(name = "creator_id")
    private Users creatorId;

    @Column(name = "assigned_mentor_id")
    private Users assignedMentorId;

    @Column(name = "issue_title")
    private String issueTitle;

    @Column(name = "issue_description", columnDefinition = "TEXT")
    private String issueDescription;

    @Column(name = "tech_tags", length = 100)
    private String techTags;

    @Column(name = "contact_location", length = 255)
    private String contactLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
