package com.hackathon.HackSync.admin_core.controller;

import com.hackathon.HackSync.admin_core.service.AdminService;
import com.hackathon.HackSync.admin_core.dto.PlatformMetricsDto;
import com.hackathon.HackSync.auth.entity.Users;
import com.hackathon.HackSync.host_core.entity.Hackathons;
import com.hackathon.HackSync.utils.dto.ApiResponse;
import com.hackathon.HackSync.participants_core.dto.HackathonDetailResponseDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/hackathons/pending")
    public ResponseEntity<ApiResponse<List<HackathonDetailResponseDTO>>> getPendingHackathons() {
        return ResponseEntity.ok(new ApiResponse<>("Fetched pending hackathons successfully", HttpStatus.OK, adminService.getPendingHackathons()));
    }

    @PutMapping("/hackathons/{id}/approve")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> approveHackathon(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Hackathon approved successfully", HttpStatus.OK, adminService.approveHackathon(id)));
    }

    @PutMapping("/hackathons/{id}/reject")
    public ResponseEntity<ApiResponse<HackathonDetailResponseDTO>> rejectHackathon(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Hackathon rejected successfully", HttpStatus.OK, adminService.rejectHackathon(id)));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<PlatformMetricsDto>> getPlatformMetrics() {
        return ResponseEntity.ok(new ApiResponse<>("Platform metrics fetched successfully", HttpStatus.OK, adminService.getPlatformMetrics()));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse<Users>> banUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("User banned successfully", HttpStatus.OK, adminService.banUser(id)));
    }

    /*
     * GET /api/v1/admin/hackathons/pending - Retrieves the queue of all hackathons
     * waiting in PENDING_APPROVAL status.
     * PUT /api/v1/admin/hackathons/{id}/approve - Changes a hackathon's status to
     * APPROVED, making it visible on the platform feed.
     * PUT /api/v1/admin/hackathons/{id}/reject - Denies a hackathon from being
     * listed.
     * GET /api/v1/admin/metrics - Fetches high-level platform statistics (total
     * active hackathons, total registered users, total submissions).
     * PUT /api/v1/admin/users/{id}/ban - Removes or disables a user (Host or
     * Participant) from the platform for unethical behavior.
     * GET /actuator/health - Get System health
     * GET /actuator/metrics - get metrics of the system
     * GET /actuator/info - get info of the application
     * GET /actuator/httpexchanges - Get all the HTTP exchanges
     * GET /actuator/loggers - Get all the loggers
     * POST /actuator/loggers/{name} - Enable/Disable loggers
     */

    /*
     * DASHBOARD USECASES:
     * /metrics: Dashboard Use Case: Build real-time line charts or gauge widgets
     * showing server load and memory consumption.
     * /info : Display the "Current Deployment Version" at the bottom of the
     * dashboard so Admins know exactly which build is running.
     * /httpexchanges: Create a "Recent Traffic" or "Recent Errors" table. If the
     * Admin sees a spike in 500 Internal Server Error responses, they know
     * something is breaking in real-time.
     * /loggers: Add a dropdown for the Admin. If there is a sudden bug in
     * production, the Admin can change the log level of com.hackathon.HackSync from
     * INFO to DEBUG via the dashboard, capture the error, and switch it back to
     * INFO before the logs fill up the disk.
     * 
     */

}
