package com.hackathon.HackSync.auth.service;

import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> allUsers() {
        return userRepository.findAll();
    }
}
