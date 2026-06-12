package com.hackathon.HackSync.mentor_core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/mentor")
public class MentorController {
    /*
    GET /api/v1/mentor/tickets/open - Queries the help_tickets table for all requests currently marked as OPEN.
    PUT /api/v1/mentor/tickets/{id}/claim - Updates the ticket by adding the mentor_id, pasting the manual Google Meet link, and changing the status to CLAIMED.
    PUT /api/v1/mentor/tickets/{id}/resolve - Updates the resolved_at timestamp and closes out the ticket once the mentor finishes the call.
    GET /api/v1/mentor/tickets/me - Fetches a history of all tickets claimed and resolved by this specific mentor.
     */
}
