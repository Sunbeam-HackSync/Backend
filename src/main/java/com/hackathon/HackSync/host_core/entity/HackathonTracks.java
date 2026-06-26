package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "hackathon_track_id"))
@Table(name = "hackathon_tracks")
@Getter
@Setter
@ToString(callSuper = true, exclude = "hackathonId")
public class HackathonTracks extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}
