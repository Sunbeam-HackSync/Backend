package com.hackathon.HackSync.mentor_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonId", "mentorsId"})
@Table(name = "hackathons_mentors")
public class HackathonsMentors extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Users mentorsId;

    @Column(name = "expertise_tags")
    private String expertiseTags;

    @Enumerated(EnumType.STRING)
    private MentorStatus status;

}
