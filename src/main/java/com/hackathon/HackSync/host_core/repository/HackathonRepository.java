package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.hackathon.HackSync.auth.entity.Users;
import java.util.List;

public interface HackathonRepository extends JpaRepository<Hackathons, UUID> {
    List<Hackathons> findByHostId(Users hostId);
}
