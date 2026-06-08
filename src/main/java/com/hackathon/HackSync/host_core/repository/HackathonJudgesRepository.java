package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonJudges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HackathonJudgesRepository extends JpaRepository<HackathonJudges, UUID> {
}
