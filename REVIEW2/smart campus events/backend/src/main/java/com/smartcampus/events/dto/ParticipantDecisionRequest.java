package com.smartcampus.events.dto;

import jakarta.validation.constraints.NotNull;

public class ParticipantDecisionRequest {
    @NotNull
    private Long registrationId;
    @NotNull
    private boolean approve;

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public boolean isApprove() { return approve; }
    public void setApprove(boolean approve) { this.approve = approve; }
}
