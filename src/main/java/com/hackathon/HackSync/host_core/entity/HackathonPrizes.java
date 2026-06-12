package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonId", "trackId"})
@Table(name = "hackathon_prizes")
@AttributeOverride(name = "id", column = @Column(name = "hackathon_prize_id"))
public class HackathonPrizes extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private HackathonTracks trackId;

    @Column(name = "title")
    private String priceTitle;

    @Column(name = "reward_value")
    private String rewardValue;

    @Column(columnDefinition = "TEXT")
    private String description;
}
