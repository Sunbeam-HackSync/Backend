package com.hackathon.HackSync.host_core.service;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HackathonStatusScheduler {

    private final HackathonRepository hackathonRepository;

    /**
     * This scheduled task runs every minute to check if any APPROVED hackathons
     * have reached their start date/time. If they have, it sets them to ACTIVE.
     */
    @Scheduled(cron = "0 * * * * *") // Runs at second 0 of every minute
    @Transactional
    public void checkAndActivateHackathons() {
        LocalDateTime now = LocalDateTime.now();

        // Find all hackathons that are APPROVED but their start time has already passed
        List<Hackathons> hackathonsToStart = hackathonRepository.findByHackathonStatusAndHackathonStartBefore(
                HackathonStatus.APPROVED, now);

        if (!hackathonsToStart.isEmpty()) {
            log.info("Found {} hackathon(s) ready to start. Activating them now...", hackathonsToStart.size());

            for (Hackathons hackathon : hackathonsToStart) {
                hackathon.setHackathonStatus(HackathonStatus.ACTIVE);
                hackathonRepository.save(hackathon);
                log.info("Hackathon '{}' (ID: {}) is now ACTIVE", hackathon.getTitle(), hackathon.getId());
            }
        }
    }

    /**
     * This scheduled task runs every minute to check if any ACTIVE hackathons
     * have reached their end date/time. If they have, it sets them to COMPLETED.
     */
    @Scheduled(cron = "0 * * * * *") // Runs at second 0 of every minute
    @Transactional
    public void checkAndCompleteHackathons() {
        LocalDateTime now = LocalDateTime.now();

        // Find all hackathons that are ACTIVE but their end time has already passed
        List<Hackathons> hackathonsToEnd = hackathonRepository.findByHackathonStatusAndHackathonEndBefore(
                HackathonStatus.ACTIVE, now);

        if (!hackathonsToEnd.isEmpty()) {
            log.info("Found {} hackathon(s) ready to end. Completing them now...", hackathonsToEnd.size());

            for (Hackathons hackathon : hackathonsToEnd) {
                hackathon.setHackathonStatus(HackathonStatus.COMPLETED);
                hackathonRepository.save(hackathon);
                log.info("Hackathon '{}' (ID: {}) is now COMPLETED", hackathon.getTitle(), hackathon.getId());
            }
        }
    }
}
