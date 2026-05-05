package com.smartcampus.events.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EventRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate date;
    @NotBlank
    private String department;
    @NotBlank
    private String type;
    @NotNull
    @Min(1)
    private Integer seats;
    @NotBlank
    private String organizer;
    private String status;
    private boolean featured;

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
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}
