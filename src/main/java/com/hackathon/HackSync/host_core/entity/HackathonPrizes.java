package com.hackathon.HackSync.host_core.entity;

import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"hackathonId"})
@Table(name = "hackathon_prizes")
@AttributeOverride(name = "id", column = @Column(name = "hackathon_prize_id"))
public class HackathonPrizes extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathons hackathonId;
    
    @Column(name = "title")
    private String priceTitle;

    @Column(name = "reward_value")
    private String rewardValue;

    @Column(columnDefinition = "TEXT")
    private String description;
}
