package rs.raf.dto;

public class CommentDto {
    private int id;
    private String author;
    private String text;
    private int event_id;
    private int like_count;

    public CommentDto() {
    }

    public CommentDto(int id, String author, String text, int event_id, int like_count) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.event_id = event_id;
        this.like_count = like_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}


