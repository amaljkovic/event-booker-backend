package rs.raf.repositories.events;

import rs.raf.dto.CommentDto;
import rs.raf.dto.EventDto;
import rs.raf.dto.ReactionDto;
import rs.raf.dto.TagDto;
import rs.raf.entities.Event;
import rs.raf.entities.Tag;

import java.util.List;

public interface EventRepository {
    List<Event> getAllEvents(int limit, int offset);
    List<Event> getEventsSortedByViews(int limit);
    List<Event> getEventsByKeyword(String keyword, int limit, int offset);
    Event getEventById(int id, Integer userId, String visitor);
    EventDto createEvent(EventDto event);
    EventDto editEvent(EventDto event);
    void deleteEvent(int id);
    Event addComment(CommentDto comment);
    Event addTag(TagDto tag);
    String commentReaction(ReactionDto c, Integer userId, String visitorId);
    Event rsvpToEvent(int eventId, Integer userId, String visitorId);
    List<Event> filterByCategory(int id, int limit, int offset);
    List<Event> getEventsByTag(int tagId, int limit, int offset);
    int countAllEvents();
    int countEventsByCategory(int id);
    int countEventsByTag(int tagId);
    int countEventsByKeyword(String keyword);

    Event eventReaction(ReactionDto r, Integer userId, String visitorId);

    List<Event> mostReactions(int limit);
}
