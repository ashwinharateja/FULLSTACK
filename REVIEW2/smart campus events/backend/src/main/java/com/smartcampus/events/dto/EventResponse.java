package com.smartcampus.events.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private String department;
    private String type;
    private Integer seats;
    private Long registeredCount;
    private Long seatsAvailable;
    private boolean registered;
    private boolean waitlisted;
    private boolean bookmarked;
    private String organizer;
    private String status;
    private boolean featured;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    public Long getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(Long registeredCount) { this.registeredCount = registeredCount; }
    public Long getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(Long seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public boolean isRegistered() { return registered; }
    public void setRegistered(boolean registered) { this.registered = registered; }
    public boolean isWaitlisted() { return waitlisted; }
    public void setWaitlisted(boolean waitlisted) { this.waitlisted = waitlisted; }
    public boolean isBookmarked() { return bookmarked; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
