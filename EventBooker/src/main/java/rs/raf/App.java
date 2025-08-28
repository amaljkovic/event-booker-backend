package rs.raf;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rs.raf.entities.User;
import rs.raf.services.CategoryService;
import rs.raf.services.EventService;
import rs.raf.services.UserService;
import rs.raf.util.CookieHelper;
import spark.Session;

import java.util.Objects;

import static spark.Spark.*;

public class App 
{
    public static void main( String[] args )
    {
        String plain = "test";
        String hash = BCrypt.withDefaults().hashToString(12, plain.toCharArray());
        System.out.println(hash);
        port(8080);

        get("/hello",(req,res)-> "Hello World");

        UserService userService = new UserService();
        EventService eventService = new EventService();
        CategoryService categoryService = new CategoryService();

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        defaultResponseTransformer(mapper::writeValueAsString);


        before((req, res) -> {
            if (req.session(false) == null || req.session().attribute("id") == null) {
                String visitorId = CookieHelper.getOrCreateVisitorId(req, res);
                req.attribute("visitorId", visitorId);
            }
        });


        //users
        before("/api/users",(req,res)->{
            if(req.session().attribute("id")==null)
                halt(401,"Not authenticated");
            boolean role = req.session().attribute("role");
            if(!role){
                halt(403,"Not authorized");
            }
        });
        before("/api/users/*",(req,res)->{
            if(req.session().attribute("id")==null)
                halt(401,"Not authenticated");
            boolean role = req.session().attribute("role");
            if(!role){
                halt(403,"Not authorized");
            }
        });
        //categories
        before("/api/categories",(req,res)->{
            if(req.requestMethod().equals("GET"))
                return;

                //neulogovani useri smeju get
            if(req.session().attribute("id")==null)
                halt(401,"Not authenticated");
            if(!req.requestMethod().equals("GET")) {
                if (!userService.isActive(req, res))
                    halt(403, "Inactive User");
            }
        });
        before("/api/categories/*",(req,res)->{
            if(req.requestMethod().equals("GET"))
                return;
            if(req.session().attribute("id")==null)
                halt(401,"Not authenticated");
            if(!req.requestMethod().equals("GET")) {
                if (!userService.isActive(req, res))
                    halt(403, "Inactive User");
            }
        });
        //events
        before("/api/events",(req,res)->{
            String path = req.pathInfo();

            if (path.startsWith("/api/events/comments") || path.startsWith("/api/events/reaction/")) {
                return;
            }
            if(req.session().attribute("id")==null && req.attribute("visitorId")==null )
                halt(401,"Not authenticated");
            if(!req.requestMethod().equals("GET")) {
                if (!userService.isActive(req, res))
                    halt(403, "Inactive User");
            }
        });
        before("/api/events/*",(req,res)->{
            String path = req.pathInfo();

            if (path.startsWith("/api/events/comments") || path.startsWith("/api/events/reaction/") || path.startsWith("/api/me")) {
                return;
            }
            if(!req.requestMethod().equals("GET")) {
                if(req.session().attribute("id")==null)
                    halt(401,"Not authenticated");
                if(!req.requestMethod().equals("GET")) {
                    if (!userService.isActive(req, res))
                        halt(403, "Inactive User");
                }
            }
        });

        path("/api", () -> {
            get("/me",userService::me);
            post("/login",(req,res)->{
                User user = userService.login(req,res);
                if(user == null) {
                    res.status(401);
                    res.type("application/json");
                    return "Invalid email or password";
                }
                else{
                    Session session = req.session(true);
                    session.attribute("role", user.isRole());
                    session.attribute("id", user.getId());
                    session.maxInactiveInterval(10000000);
                    System.out.println("LOGIN sessionId=" + session.id() + " userId=" + user.getId());
                    res.status(200);
                    return "Logged in successfully";
                }
            });

            path("/events", () -> {
                get("", eventService::getEvents);
                get("/most-reactions",eventService::mostReacted);
                get("/rsvp/:id", eventService::rsvp);
                get("/search/:keyword",eventService::searchEvents);
                get("/category-filter/:id",eventService::filterCategory);
                get("/views",eventService::getEventsByViews);
                get("/:id",eventService::getEventById);
                post("/:id",eventService::editEvent);
                post("", eventService::addEvent);
                put("/:id",eventService::editEvent);
                delete("/:id",eventService::deleteEvent);
                post("/comments/reaction/:id",eventService::commentReaction);
                post("/reaction/:id",eventService::eventReaction);


                post("/comments/:id",eventService::addComment); //todo tests
                get("/tag/:id",eventService::getEventsByTag);
                post("/tag/:id",eventService::addTag);
                //todo put comment/reaction
            });

            path("/users",()->{
                get("",userService::getUsers);
                get("/:id",userService::getUserById);
                put("/activity/:id",(req,res)->{
                    userService.changeActivity(req,res);
                    res.status(200);
                    return "User active status changed.";
                });
                post("",userService::createUser);
                put("/:id",userService::updateUser);
            });

            path("/categories",()->{
                get("", categoryService::getCategories);
                get("/:id",categoryService::getCategoryById);
                post("",categoryService::addCategory);
                put("/:id",categoryService::editCategory);
                delete("/:id", categoryService::delete);
            });
        });

    }
}
