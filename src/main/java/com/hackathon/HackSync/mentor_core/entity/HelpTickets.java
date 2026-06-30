package com.hackathon.HackSync.mentor_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.participants_core.entity.Teams;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = { "hackathonId", "teamId", "creatorId", "assignedMentorId" })
@Table(name = "help_tickets")
public class HelpTickets extends BaseClass {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Users creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_mentor_id")
    private Users assignedMentorId;

    @Column(name = "issue_title")
    private String issueTitle;

    @Column(name = "issue_description", columnDefinition = "TEXT")
    private String issueDescription;

    @Column(name = "tech_tags", length = 100)
    private String techTags;

    @Column(name = "contact_location", length = 255)
    private String contactLocation;

    @Column(name = "meeting_link", length = 512)
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
