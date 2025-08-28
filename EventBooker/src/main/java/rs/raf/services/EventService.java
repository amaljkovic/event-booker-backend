package rs.raf.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rs.raf.dto.*;
import rs.raf.entities.Event;
import rs.raf.repositories.events.EventRepository;
import rs.raf.repositories.events.EventRepositoryImpl;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.List;

public class EventService {

    private EventRepository eventRepository = new EventRepositoryImpl();
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Page<Event> getEvents(Request req, Response res) {
        int page = Math.max(1, qInt(req, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(req, "size", 10)));
        int offset = (page - 1) * size;

        List<Event> items = eventRepository.getAllEvents(size,offset);
        int total = eventRepository.countAllEvents();

        return new Page<>(items, page, size, total);
    }

    public Event getEventById(Request req, Response res) {
        int id = Integer.parseInt(req.params(":id"));

        Integer userId = null;
        if (req.session(false) != null) {
            userId = req.session().attribute("id");
        }

        if (userId != null) {
            return eventRepository.getEventById(id, userId, null);
        }

        String visitorId = req.attribute("visitorId");
        return eventRepository.getEventById(id, null, visitorId);
    }

    public Event rsvp(Request req, Response res) throws JsonProcessingException {
        int id = Integer.parseInt(req.params(":id"));
        Integer userId = null;
        if (req.session(false) != null) {
            userId = req.session().attribute("id");
            return eventRepository.rsvpToEvent(id,userId,null);
        }
        String visitorId = req.attribute("visitorId");
        return eventRepository.rsvpToEvent(id,null,visitorId);
    }

    public EventDto addEvent(Request req, Response res) throws JsonProcessingException {
        if (req.body() == null || req.body().trim().isEmpty()) {
            res.status(400);
            return null;
        }
        Session session = req.session(false);
        Integer authorId = (session == null) ? null : session.attribute("id");
        System.out.println(
                "[/api/events] session=" + (session == null ? "<none>" : session.id()) +
                        " authorId=" + authorId
        );

        if (authorId == null) {
            res.status(401);
            res.type("text/plain");
            return null;
        }

        EventDto event = mapper.readValue(req.body(), EventDto.class);
        event.setAuthor_id(authorId);
        EventDto e = eventRepository.createEvent(event);
        if(event.getTags() != null) {
            for (String tag : event.getTags()) {
                if(tag == null) {
                    continue;
                }
                for(String part : tag.split(",")) {
                    String kw = part.trim();
                    if(!kw.isEmpty()) {
                        TagDto t = new TagDto(kw,e.getId());
                        eventRepository.addTag(t);
                    }
                }
            }
        }
        return e;
    }

    public EventDto editEvent(Request req, Response res) throws JsonProcessingException {
        if (req.body() == null || req.body().trim().isEmpty()) {
            res.status(400);
            return null;
        }
        EventDto event = mapper.readValue(req.body(), EventDto.class);
        event.setId(Integer.parseInt(req.params(":id")));
        return eventRepository.editEvent(event);
    }

    public String deleteEvent(Request request, Response response) {
        int eventId = Integer.parseInt(request.params(":id"));
        eventRepository.deleteEvent(eventId);
        return "Event deleted";
    }

    public Page<Event> searchEvents(Request req, Response res) {
        String keyword = req.params("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            res.status(400);
            return null;
        }
        int page = Math.max(1, qInt(req, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(req, "size", 10)));
        int offset = (page - 1) * size;

        List<Event> items = eventRepository.getEventsByKeyword(keyword, size, offset);
        int count = eventRepository.countEventsByKeyword(keyword);

        return new Page<Event>(items,page,size,count);
    }

    public Event addComment(Request req, Response res) throws JsonProcessingException {
        int id = Integer.parseInt(req.params(":id"));
        if (req.body() == null || req.body().trim().isEmpty()) {
            res.status(400);
            return null;
        }
        try{
            CommentDto commentDto = mapper.readValue(req.body(), CommentDto.class);
            commentDto.setEvent_id(id);
            return eventRepository.addComment(commentDto);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String commentReaction(Request req, Response res) throws JsonProcessingException {
    //msm da cu setovati na frontu like=+1 dislike=-1
        int id = Integer.parseInt(req.params(":id"));
        ReactionDto r =mapper.readValue(req.body(), ReactionDto.class);
        r.setComment_id(id);
        if (req.body() == null || req.body().trim().isEmpty()) {
            res.status(400);
            return null;
        }
        Integer userId = null;
        if (req.session(false) != null) {
            userId = req.session().attribute("id");
        }

        if (userId != null) {
            return eventRepository.commentReaction(r,userId,null);
        }
        String visitorId = req.attribute("visitorId");
        return eventRepository.commentReaction(r,null,visitorId);

    }



    public Event addTag(Request req, Response res) throws JsonProcessingException {
        int id = Integer.parseInt(req.params(":id"));
        if (req.body() == null || req.body().trim().isEmpty()) {
            res.status(400);
            return null;
        }
        TagDto tagDto = mapper.readValue(req.body(), TagDto.class);
        tagDto.setEvent_id(id);
        return eventRepository.addTag(tagDto);
    }

    public List<Event> getEventsByViews(Request req, Response res) {


        return eventRepository.getEventsSortedByViews(10);

    }

    public Page<Event> filterCategory(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));

        int page = Math.max(1, qInt(request, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(request, "size", 10)));
        int offset = (page - 1) * size;

        List<Event> items = eventRepository.filterByCategory(id, size, offset);
        int count = eventRepository.countEventsByCategory(id);

        return new Page<Event>(items,page,size,count);
    }

    public Page<Event> getEventsByTag(Request req, Response res) {
        int id = Integer.parseInt(req.params(":id"));

        int page = Math.max(1, qInt(req, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(req, "size", 10)));
        int offset = (page - 1) * size;

        List<Event> items = eventRepository.getEventsByTag(id, size, offset);
        int count = eventRepository.countEventsByTag(id);

        return new Page<Event>(items,page,size,count);
    }

    private int qInt(Request req, String name, int def) {
        try { return Integer.parseInt(req.queryParams(name)); } catch (Exception e) { return def; }
    }

    public Object eventReaction(Request request, Response response) throws JsonProcessingException {
        int id = Integer.parseInt(request.params(":id"));
        ReactionDto r =mapper.readValue(request.body(), ReactionDto.class);
        r.setEvent_id(id);
        if (request.body() == null || request.body().trim().isEmpty()) {
            response.status(400);
            return null;
        }
        Integer userId = null;
        if (request.session(false) != null) {
            userId = request.session().attribute("id");
        }

        if (userId != null) {
            return eventRepository.eventReaction(r,userId,null);
        }
        String visitorId = request.attribute("visitorId");
        return eventRepository.eventReaction(r,null,visitorId);
    }

    public Object mostReacted(Request request, Response response) {
        return eventRepository.mostReactions(3);
    }
}
