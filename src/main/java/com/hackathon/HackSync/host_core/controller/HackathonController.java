package com.hackathon.HackSync.host_core.controller;

import com.hackathon.HackSync.host_core.dto.HackathonRequestDTO;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.host_core.responses.HackathonResponse;
import com.hackathon.HackSync.host_core.service.HackathonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/host/hackathon")
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/create")
    public ResponseEntity<HackathonResponse> createHackathon(@RequestBody HackathonRequestDTO hackathon, Principal principal) {
        String email = principal.getName();

        HackathonResponse hackathonResponse = hackathonService.createHackathon(hackathon, email);
        return new ResponseEntity<>(hackathonResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHackathonById(@RequestParam UUID id, Principal principal) {
        String email = principal.getName();
        Hackathons hackathon = hackathonService.getHackathonById(id, email);
        return new ResponseEntity<>(hackathon, HttpStatus.OK);
    }
}
    /*
        PUT /api/v1/host/hackathons/{id} - Updates the details of a drafted or active hackathon.
        GET /api/v1/host/hackathons/me - Lists all hackathons created by this specific host.
        GET /api/v1/host/hackathons/{id}/participants - Returns a list of all registered users for their specific event.
        GET /api/v1/host/hackathons/{id}/submissions - Returns all static project data submitted before the deadline.
        POST /api/v1/host/assignments - Creates a record in the judge_assignments table to map a specific submission to a specific judge.
        GET /api/v1/host/hackathons/{id}/scores - Fetches the aggregated numerical scores submitted by all judges for the host to review.
        PUT /api/v1/host/hackathons/{id}/publish - Changes the hackathon status to COMPLETED and makes the winning results public.
     */
