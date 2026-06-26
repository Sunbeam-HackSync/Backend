package com.hackathon.HackSync.admin_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = { "reporterId", "resolvedBy" })
@Table(name = "platform_reports")
@AttributeOverride(name = "id", column = @Column(name = "platform_reports_id"))
public class PlatformReports extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Users reporterId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private PlatformStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private Users resolvedBy;

}
