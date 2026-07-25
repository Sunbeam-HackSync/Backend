package com.hackathon.HackSync.mentor_core.repository;

import com.hackathon.HackSync.mentor_core.entity.HackathonsMentors;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonsMentorsRepository extends JpaRepository<HackathonsMentors, Long> {
    Optional<HackathonsMentors> findByHackathonId_IdAndMentorsId_Id(Long hackathonId, Long mentorId);
}
