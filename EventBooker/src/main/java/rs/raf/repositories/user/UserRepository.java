package rs.raf.repositories.user;

import rs.raf.entities.User;

import java.util.List;

public interface UserRepository {
    List<User> getUsers(int limit, int offset);
    User createUser(User user);
    User editUser(User user);
    void activateUser(int id);
    User findUserByEmail(String email);
    User getUserById(int id);
    int countUsers();
}
