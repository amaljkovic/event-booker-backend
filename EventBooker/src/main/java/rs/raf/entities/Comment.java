package rs.raf.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Comment {
    private int id;
    @JsonProperty("author")
    private String comment_author;
    private String text;
    Date creation_date;
    private int like_count = 0;

    public Comment() {
    }

    public Comment(int id, String comment_author, String text, Date creation_date, int like_count) {
        this.id = id;
        this.comment_author = comment_author;
        this.text = text;
        this.creation_date = creation_date;
        this.like_count = like_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment_author() {
        return comment_author;
    }

    public void setComment_author(String comment_author) {
        this.comment_author = comment_author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}
