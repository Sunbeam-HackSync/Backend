package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.HackSync.auth.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathons, Long> {
    List<Hackathons> findByHostId(Users hostId);

    List<Hackathons> findByHackathonStatus(HackathonStatus status);

    long countByHackathonStatus(HackathonStatus status);

    Page<Hackathons> findByHackathonStatusIn(List<HackathonStatus> statuses, Pageable pageable);
}
