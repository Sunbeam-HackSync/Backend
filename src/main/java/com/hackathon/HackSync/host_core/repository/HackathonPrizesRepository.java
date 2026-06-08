package com.hackathon.HackSync.host_core.repository;

import com.hackathon.HackSync.host_core.entity.HackathonPrizes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HackathonPrizesRepository extends JpaRepository<HackathonPrizes, UUID> {
}
