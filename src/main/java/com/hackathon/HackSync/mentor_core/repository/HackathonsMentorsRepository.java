package com.hackathon.HackSync.mentor_core.repository;

import com.hackathon.HackSync.mentor_core.entity.HackathonsMentors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonsMentorsRepository extends JpaRepository<HackathonsMentors, Long> {
}
