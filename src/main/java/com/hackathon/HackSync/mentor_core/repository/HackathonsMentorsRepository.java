package com.hackathon.HackSync.mentor_core.repository;

import com.hackathon.HackSync.mentor_core.entity.HackathonsMentors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HackathonsMentorsRepository extends JpaRepository<HackathonsMentors, UUID> {
}
