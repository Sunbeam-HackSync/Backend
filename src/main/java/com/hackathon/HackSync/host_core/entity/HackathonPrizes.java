package com.hackathon.HackSync.host_core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "hackathon_prizes")
public class HackathonPrizes {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hackathon_id")
    //TODO mapping to hackathon table
    private Hackathons hackathonId;

    @Column(name = "track_id")
    //TODO mapping to hackathon table
    private HackathonTracks trackId;

    @Column(name = "title")
    private String priceTitle;

    @Column(name = "reward_value")
    private String rewardValue;

    @Column(columnDefinition = "TEXT")
    private String description;
    // id
    //hackthon_id
    //track_id
    //title
    //reward_value
    //description
}
