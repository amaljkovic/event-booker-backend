package rs.raf.entities;

import java.util.Date;

public class Rsvp {
    private int id;
    private int event_id;
    private Date date;
    private int user_id;
    private String visitor_id;

    public Rsvp() {}

    public Rsvp(int event_id, int user_id, String visitor_id) {
        this.event_id = event_id;
        this.user_id = user_id;
        this.visitor_id = visitor_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
