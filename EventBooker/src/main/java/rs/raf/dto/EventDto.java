package rs.raf.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class EventDto {
    private int id;
    private String title;
    private String description;
    private LocalDate event_date;
    private String location;
    private int author_id;
    private List<String> tags;
    private int category_id;
    private int max_capacity;
    private int rsvp_count;
    int reactions;

    public int getReactions() {
        return reactions;
    }

    public void setReactions(int reactions) {
        this.reactions = reactions;
    }

    public int getRsvp_count() {
        return rsvp_count;
    }

    public void setRsvp_count(int rsvp_count) {
        this.rsvp_count = rsvp_count;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getEvent_date() {
        return  event_date;
    }

    public String getLocation() {
        return location;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEvent_date(LocalDate event_date) {
        this.event_date = event_date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity = max_capacity;
    }

    public EventDto() {}

    public EventDto(String title, LocalDate event_date, String location, int author_id,
                    String description, int category_id, int max_capacity) {
        this.title = title;
        this.event_date = event_date;
        this.location = location;
        this.author_id = author_id;
        this.description = description;
        this.category_id = category_id;
        this.max_capacity = max_capacity;
    }
}
