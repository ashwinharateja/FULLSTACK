package com.smartcampus.events.dto;

public class StatsResponse {
    private long totalEvents;
    private long totalRegistrations;
    private String mostPopularEvent;
    private long activeUsers;
    private long waitlistedCount;
    private long publishedEvents;

    public long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }
    public long getTotalRegistrations() { return totalRegistrations; }
    public void setTotalRegistrations(long totalRegistrations) { this.totalRegistrations = totalRegistrations; }
    public String getMostPopularEvent() { return mostPopularEvent; }
    public void setMostPopularEvent(String mostPopularEvent) { this.mostPopularEvent = mostPopularEvent; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public long getWaitlistedCount() { return waitlistedCount; }
    public void setWaitlistedCount(long waitlistedCount) { this.waitlistedCount = waitlistedCount; }
    public long getPublishedEvents() { return publishedEvents; }
    public void setPublishedEvents(long publishedEvents) { this.publishedEvents = publishedEvents; }
}
