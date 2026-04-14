package rs.raf.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.raf.dto.Page;
import rs.raf.entities.Category;
import rs.raf.entities.User;
import rs.raf.repositories.user.UserRepository;
import rs.raf.repositories.user.UserRepositoryImpl;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private int qInt(Request req, String name, int def) {
        try { return Integer.parseInt(req.queryParams(name)); } catch (Exception e) { return def; }
    }

    private UserRepository userRepository = new UserRepositoryImpl();
    ObjectMapper mapper = new ObjectMapper();

    public Page<User> getUsers(Request req, Response res) {
        int page = Math.max(1, qInt(req, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(req, "size", 10)));
        int offset = (page - 1) * size;

        List<User> categories = userRepository.getUsers(size,offset);
        int count  = userRepository.countUsers();

        return new Page<>(categories, page, size, count);
    }

    public User createUser(Request req, Response res) throws JsonProcessingException {
        if (req.body() == null || req.body().isEmpty()) {
            res.status(400);
            return null;
        }

        User user = mapper.readValue(req.body(), User.class);

        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getName() == null || user.getName().isEmpty() ||
                user.getLast_name() == null || user.getLast_name().isEmpty()) {
            res.status(400);
            return null;
        }

        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            res.status(409);
            return null;
        }

        String hash = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(hash);


        if (!user.isActive())
            user.setActive(true);

        User created = userRepository.createUser(user);

        created.setPassword(null);

        res.status(201);
        return created;
    }

    public User updateUser(Request req, Response res) {
        User user = mapper.convertValue(req.body(), User.class);
        user.setId(Integer.parseInt(req.params(":id")));
        return userRepository.editUser(user);
    }

    public User getUserById(Request req, Response res) {
        int id = Integer.parseInt(req.params(":id"));
        return userRepository.getUserById(id);
    }

    public void changeActivity(Request req, Response res) {
        int id = Integer.parseInt(req.params(":id"));
        userRepository.activateUser(id);
    }

    public Object me(Request req, Response res) {
        Session s = req.session(false);
        if (s == null) {
            res.status(401);
            res.type("text/plain");
            return "";        // empty body instead of "null"
        }

        Integer id = s.attribute("id");
        if (id == null) {
            res.status(401);
            res.type("text/plain");
            return "";
        }

        User u = userRepository.getUserById(id);
        if (u == null || !u.isActive()) {
            res.status(401);
            res.type("text/plain");
            return "";
        }

        u.setPassword(null);
        res.status(200);
        res.type("application/json");
        return u;
    }

    public User login(Request req, Response res) {
        try {
            if (req.body() == null || req.body().isEmpty()) {
                res.status(400);
                return null;
            }
            JsonNode json = mapper.readTree(req.body());

            String email = json.get("email").asText();
            String password = json.get("password").asText();

            User user = userRepository.findUserByEmail(email);
            if (user == null || !user.isActive()) {
                res.status(401);
                return null;
            }
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if(!result.verified) {
                res.status(401);
                return null;
            }
            req.session(true).attribute("id", user.getId());
            req.session().attribute("role", user.isRole());
            res.status(200);
            return user;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isActive(Request req, Response res) {
        if(req.session()==null || req.session().attribute("id")==null)
            return false;
        User user = userRepository.getUserById(req.session().attribute("id"));
        return user.isActive();
    }
}
