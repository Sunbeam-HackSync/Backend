package com.hackathon.HackSync.admin_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.utils.entities.BaseClass;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "actorId")
@Entity
@Table(name = "platform_audit_logs")
@AttributeOverride(name = "id", column = @Column(name = "audit_logs_id"))
public class PlatformAuditLogs extends BaseClass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private Users actorId;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "ip_address")
    private String ipAddress;

}
