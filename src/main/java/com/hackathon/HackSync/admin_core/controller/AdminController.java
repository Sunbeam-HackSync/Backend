package com.hackathon.HackSync.admin_core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    /*
        GET /api/v1/admin/hackathons/pending - Retrieves the queue of all hackathons waiting in PENDING_APPROVAL status.
        PUT /api/v1/admin/hackathons/{id}/approve - Changes a hackathon's status to APPROVED, making it visible on the platform feed.
        PUT /api/v1/admin/hackathons/{id}/reject - Denies a hackathon from being listed.
        GET /api/v1/admin/metrics - Fetches high-level platform statistics (total active hackathons, total registered users, total submissions).
        PUT /api/v1/admin/users/{id}/ban - Removes or disables a user (Host or Participant) from the platform for unethical behavior.
     */
}
