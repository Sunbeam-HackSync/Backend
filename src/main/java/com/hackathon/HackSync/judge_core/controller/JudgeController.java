package com.hackathon.HackSync.judge_core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/judge")
public class JudgeController {
    /*
        GET /api/v1/judge/assignments - Fetches the clean queue of projects assigned to this specific judge where is_evaluated is FALSE.
        POST /api/v1/judge/scores - Submits the numerical score_value and optional text feedback into the scores table, and flips the assignment flag to TRUE.
    */
}
