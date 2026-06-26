package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HackathonTrackRepository extends JpaRepository<HackathonTracks, Long> {
}
