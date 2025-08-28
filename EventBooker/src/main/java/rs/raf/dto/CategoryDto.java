package rs.raf.dto;

public class CategoryDto {
    private int id;
    private String name;
    private String description;

    public CategoryDto() {
    }
    public CategoryDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
