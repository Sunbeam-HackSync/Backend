package com.hackathon.HackSync.auth.repository;

import com.hackathon.HackSync.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}
