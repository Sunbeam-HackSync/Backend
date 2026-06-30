package com.hackathon.HackSync.participants_core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMemberRequestDTO {
    private String email;   // Email of the participant to be added
}