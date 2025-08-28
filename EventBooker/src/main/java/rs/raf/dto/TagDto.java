package rs.raf.dto;

public class TagDto {
    private int id;
    private String keyword;
    private int event_id;

    public TagDto(String keyword, int event_id) {
        this.keyword = keyword;
        this.event_id = event_id;
    }

    public TagDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }
}
