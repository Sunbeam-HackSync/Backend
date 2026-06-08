package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HackathonRepository extends JpaRepository<Hackathons, UUID> {
}
