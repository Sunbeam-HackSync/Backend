package com.hackathon.HackSync.host_core.service;

import com.hackathon.HackSync.auth.entity.ROLE;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.auth.repository.UserRepository;
import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.entity.HackathonStatus;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.repository.HackathonRepository;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

    public HackathonService(HackathonRepository hackathonRepository, UserRepository userRepository) {
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
    }

    public HackathonResponse createHackathon(HackathonRequestDTO hackathonRequestDTO, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new RuntimeException("Host does not exists"));

        if (!host.getRole().equals(ROLE.HOST)) {
            // TODO throw new custom exception not allowed and return null;
        }
        Hackathons hackathon = new Hackathons();
        hackathon.setHostId(host);
        hackathon.setTitle(hackathonRequestDTO.getTitle());
        hackathon.setTagline(hackathonRequestDTO.getTagline());
        hackathon.setDescription(hackathonRequestDTO.getDescription());
        //TODO add the url
        hackathon.setBannerImageUrl(null);
        hackathon.setProfileImageUrl(null);

        hackathon.setMinTeamSize(hackathonRequestDTO.getMinTeamSize());
        hackathon.setMaxTeamSize(hackathonRequestDTO.getMaxTeamSize());

        hackathon.setRegistrationStart(hackathonRequestDTO.getRegistrationStart());
        hackathon.setRegistrationEnd(hackathonRequestDTO.getRegistrationEnd());
        hackathon.setHackathonStart(hackathonRequestDTO.getHackathonStart());
        hackathon.setHackathonEnd(hackathonRequestDTO.getHackathonEnd());

        hackathon.setHackathonStatus(HackathonStatus.DRAFT);

        Hackathons savedHackathon = hackathonRepository.save(hackathon);

        return HackathonResponse.builder()
                .id(savedHackathon.getId())
                .title(savedHackathon.getTitle())
                .tagline(savedHackathon.getTagline())
                .hackathonStatus(savedHackathon.getHackathonStatus())
                .hackathonStarts(savedHackathon.getHackathonStart())
                .hackathonEnds(savedHackathon.getHackathonEnd())
                .build();
    }

    public Hackathons getHackathonById(UUID hackId, String authenticatedEmail) {
        Users host = userRepository.findByEmail(authenticatedEmail).orElseThrow(() -> new RuntimeException("Host does not exists"));

        if (!host.getRole().equals(ROLE.HOST)) {
            // TODO throw new custom exception not allowed and return null;
        }
        return hackathonRepository.getReferenceById(hackId);
    }
}
