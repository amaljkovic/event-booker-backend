package rs.raf.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private int id;
    private String name;
    private String last_name;
    private String email;
    private String password;
    private boolean role;
    private boolean active;

    public User(){}

    public User(int id, String email, String password, String name, String last_name, boolean role, boolean active) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.role = role;  //true->admin
        this.active = active;
    }

    public User(String email, String password, String name, String last_name, boolean role, boolean active) {
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.role = role;  //true->admin
        this.active = active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(int id, String email, String name, String last_name) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("role")
    public boolean isRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}

