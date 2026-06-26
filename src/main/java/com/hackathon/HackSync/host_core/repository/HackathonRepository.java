package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import org.springframework.data.jpa.repository.JpaRepository;


import com.hackathon.HackSync.auth.entity.Users;
import java.util.List;

public interface HackathonRepository extends JpaRepository<Hackathons, Long> {
    List<Hackathons> findByHostId(Users hostId);
}
