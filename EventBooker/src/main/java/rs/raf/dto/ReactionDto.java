package rs.raf.dto;

public class ReactionDto {
    private int id;
    private int user_id;
    private String visitor_id;
    private int comment_id;
    private int reaction;
    private int event_id;

    public ReactionDto() {
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public ReactionDto(int user_id, String visitor_id, int comment_id, int reaction) {
        this.user_id = user_id;
        this.visitor_id = visitor_id;
        this.comment_id = comment_id;
        this.reaction = reaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getVisitor_id() {
        return visitor_id;
    }

    public void setVisitor_id(String visitor_id) {
        this.visitor_id = visitor_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
