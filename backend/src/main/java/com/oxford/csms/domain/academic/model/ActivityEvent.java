package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "activity_events")
public class ActivityEvent extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate eventDate;

    private String venue;

    private String targetAudience; // "All Students", "Senior Wing"

    private String organizer;

    private String description;

    private String status = "UPCOMING"; // UPCOMING, COMPLETED, POSTPONED

    public ActivityEvent() {}

    public ActivityEvent(String title, LocalDate eventDate, String venue, String targetAudience, String organizer, String description) {
        this.title = title;
        this.eventDate = eventDate;
        this.venue = venue;
        this.targetAudience = targetAudience;
        this.organizer = organizer;
        this.description = description;
        this.status = "UPCOMING";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
