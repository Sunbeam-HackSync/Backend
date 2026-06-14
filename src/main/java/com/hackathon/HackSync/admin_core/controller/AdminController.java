package com.hackathon.HackSync.admin_core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
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
