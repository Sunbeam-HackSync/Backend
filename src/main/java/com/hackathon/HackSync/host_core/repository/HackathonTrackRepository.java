package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonTracks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonTrackRepository extends JpaRepository<HackathonTracks, Long> {
}
