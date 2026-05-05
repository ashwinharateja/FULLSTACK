package com.smartcampus.events.dto;

public class RegistrationInfoResponse {
    private Long registrationId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String status;
    private boolean attendanceMarked;
    private String attendanceToken;

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isAttendanceMarked() { return attendanceMarked; }
    public void setAttendanceMarked(boolean attendanceMarked) { this.attendanceMarked = attendanceMarked; }
    public String getAttendanceToken() { return attendanceToken; }
    public void setAttendanceToken(String attendanceToken) { this.attendanceToken = attendanceToken; }
}
