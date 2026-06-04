package com.hackathon.HackSync.admin_core.entity;

import com.hackathon.HackSync.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "platform_audit_logs")
public class PlatformAuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "actor_id")
    private Users actorId;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
