package com.hackathon.HackSync.participants_core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/participants")
public class ParticipantsController {
    /*
    GET /api/v1/hackathons - Fetches a paginated list of all hackathons with the status ACTIVE or APPROVED for the discovery feed.
    GET /api/v1/hackathons/{id} - Retrieves the detailed rules, timeline, and description of a specific hackathon.
    POST /api/v1/teams - Creates a new team and automatically assigns the creator as the is_team_leader.
    GET /api/v1/teams/looking - Fetches all teams within a hackathon where is_looking_for_members is TRUE to populate the matchmaking board.
    POST /api/v1/teams/{id}/join - Adds the participant to the team_members mapping table.
    PUT /api/v1/teams/{id} - Allows the team leader to update the skills_needed string or toggle their looking status.
    POST /api/v1/tickets - Generates a new Help Ticket (Issue + Tech Stack) with an OPEN status.
    POST /api/v1/submissions - Creates the final project record (Title, Description, GitHub link, Demo Video link) tied to the team.
     */

}
