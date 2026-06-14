package com.hackathon.HackSync.participants_core.entity;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "hackathonId")
@Table(name = "teams")
@AttributeOverride(name = "id", column = @Column(name = "team_id"))
public class Teams extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "is_looking_for_members")
    private boolean isLookingForMembers = false;

    @Column(name = "skills_needed")
    private String skillsNeeded;
    
}
