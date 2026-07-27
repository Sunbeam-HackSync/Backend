package com.hackathon.HackSync.admin_core.controller;

import com.hackathon.HackSync.admin_core.service.AdminService;
import com.hackathon.HackSync.admin_core.dto.PlatformMetricsDto;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/hackathons/pending")
    public ResponseEntity<ApiResponse<List<HackathonDetailResponseDTO>>> getPendingHackathons() {
        return ResponseEntity.ok(new ApiResponse<>("Fetched pending hackathons successfully", HttpStatus.OK,
                adminService.getPendingHackathons()));
    }

    @PutMapping("/hackathons/{id}/approve")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> approveHackathon(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(required = false) String feedbackNotes) {
        return ResponseEntity.ok(new ApiResponse<>("Hackathon approved successfully", HttpStatus.OK,
                adminService.approveHackathon(id, principal.getName(), feedbackNotes)));
    }

    @PutMapping("/hackathons/{id}/reject")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> rejectHackathon(
            @PathVariable Long id,
            Principal principal,
            @RequestParam(required = false) String feedbackNotes) {
        return ResponseEntity.ok(new ApiResponse<>("Hackathon rejected successfully", HttpStatus.OK,
                adminService.rejectHackathon(id, principal.getName(), feedbackNotes)));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<PlatformMetricsDto>> getPlatformMetrics() {
        return ResponseEntity.ok(new ApiResponse<>("Platform metrics fetched successfully", HttpStatus.OK,
                adminService.getPlatformMetrics()));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse<Users>> banUser(@PathVariable Long id) {
        return ResponseEntity
                .ok(new ApiResponse<>("User banned successfully", HttpStatus.OK, adminService.banUser(id)));
    }

    /*
     * GET /actuator/health - Get System health
     * GET /actuator/metrics - get metrics of the system
     * GET /actuator/info - get info of the application
     * GET /actuator/httpexchanges - Get all the HTTP exchanges
     * GET /actuator/loggers - Get all the loggers
     * POST /actuator/loggers/{name} - Enable/Disable loggers
     */

}
