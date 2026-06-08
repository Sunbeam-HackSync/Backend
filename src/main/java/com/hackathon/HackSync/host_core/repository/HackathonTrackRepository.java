package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HackathonTrackRepository extends JpaRepository<HackathonTracks, UUID> {
}
