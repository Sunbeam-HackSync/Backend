package com.hackathon.HackSync.admin_core.repository;

import com.hackathon.HackSync.admin_core.entity.HackathonReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.HackSync.host_core.entity.Hackathons;
import java.util.Optional;

@Repository
public interface HackathonReviewsRepository extends JpaRepository<HackathonReviews, Long> {
    Optional<HackathonReviews> findByHackathonId(Hackathons hackathon);
}
