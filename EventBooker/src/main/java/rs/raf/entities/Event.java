package rs.raf.entities;

import java.util.Date;
import java.util.List;

public class Event {

    private int id;
    private String title;
    private String description;
    private Date creation_date;
    private Date event_date;
    private String location;
    private int views;
    private User author;
    private List<Tag> tags;
    private Category category;
    private int max_capacity;
    private List<Comment> comments;
    private int rsvp_count;
    private int reactions;

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

    public Event() {}

    public Event(int id, String title, String description, Date creation_date, Date event_date, String location, int views, User author, List<Tag> tags, Category category, int rsvp_count,int reactions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creation_date = creation_date;
        this.event_date = event_date;
        this.location = location;
        this.views = views;
        this.author = author;
        this.tags = tags;
        this.category = category;
        this.rsvp_count = rsvp_count;
        this.reactions = reactions;
    }

    public Event(int id, String title, String description, Date creation_date, Date event_date, String location, int views, int max_capacity,int views_count, int reactions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creation_date = creation_date;
        this.event_date = event_date;
        this.location = location;
        this.views = views;
        this.max_capacity = max_capacity;
        this.rsvp_count = views_count;
        this.reactions = reactions;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getViews() {
        return views;
    }

    public User getAuthor() {
        return author;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Category getCategory() {
        return category;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity = max_capacity;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
