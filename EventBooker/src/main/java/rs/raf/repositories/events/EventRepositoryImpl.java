package rs.raf.repositories.events;

import rs.raf.dto.CommentDto;
import rs.raf.dto.EventDto;
import rs.raf.dto.ReactionDto;
import rs.raf.dto.TagDto;
import rs.raf.entities.*;
import rs.raf.repositories.AbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventRepositoryImpl extends AbstractRepository implements EventRepository {

    public int countAllEvents(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try{
            conn = this.newConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM events");
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(ps);
            this.closeResultSet(rs);
            this.closeConnection(conn);
        }
        return 0;
    }

    @Override
    public int countEventsByCategory(int id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try{
            conn = this.newConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM events where category_id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(ps);
            this.closeResultSet(rs);
            this.closeConnection(conn);
        }
        return 0;
    }

    @Override
    public int countEventsByTag(int tagId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try{
            conn = this.newConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM tags where tag_id = ?");
            ps.setInt(1, tagId);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            this.closeStatement(ps);
            this.closeResultSet(rs);
            this.closeConnection(conn);
        }
        return 0;
    }

    @Override
    public int countEventsByKeyword(String keyword) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;

        try{
            conn = this.newConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM events where title like ? or description like ?");
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            this.closeStatement(ps);
            this.closeResultSet(rs);
            this.closeConnection(conn);
        }
        return 0;
    }

    @Override
    public String eventReaction(ReactionDto r, Integer userId, String visitorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement check = null;
        ResultSet checkRes = null;
        ResultSet resultSet = null;
        PreparedStatement update = null;
        ResultSet updateRes = null;

        try{
            connection = this.newConnection();
            if(userId != null){
                check = connection.prepareStatement("select * from comment_reactions where user_id=? and event_id=?");
                check.setInt(1, userId);
            }else{
                check = connection.prepareStatement("select * from comment_reactions where  visitor_id=? and event_id=?");
                check.setString(1, visitorId);
            }
            check.setInt(2,r.getEvent_id());

            checkRes = check.executeQuery();
            if(checkRes.next()){  //-1
                return "already thre";
            }
            if(userId != null){
                statement = connection.prepareStatement("insert  into comment_reactions (user_id,event_id,reaction) values(?,?,?)");
                statement.setInt(1, userId);
                statement.setInt(2, r.getEvent_id());
                statement.setInt(3, r.getReaction());
            }
            else{
                statement = connection.prepareStatement("insert  into comment_reactions (visitor_id,event_id,reaction) values(?,?,?)");
                statement.setString(1, visitorId);
                statement.setInt(2, r.getEvent_id());
                statement.setInt(3, r.getReaction());
            }
            statement.executeUpdate();
            update = connection.prepareStatement("update events set reaction=events.reaction+? where id=?");
            update.setInt(1,r.getReaction());
            update.setInt(2,r.getEvent_id());

            update.executeUpdate();

            return "Like count changed";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(check);
            this.closeStatement(update);
            this.closeResultSet(updateRes);
            this.closeResultSet(checkRes);
        }
    }

    @Override
    public List<Event> mostReactions(int limit) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        List<Event> events = new ArrayList<>();
        PreparedStatement tagStatement = null;
        ResultSet tagResultSet = null;

        PreparedStatement commentStatement = null;
        ResultSet commentResultSet = null;


        try {
            c = this.newConnection();
            ps = c.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description,events.rsvp_count, events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id  order by reaction desc limit ?");
            ps.setInt(1, limit);
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));

                tagStatement = c.prepareStatement("SELECT tag_entity.id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");
                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagStatement.setInt(1, resultSet.getInt(1));
                tagResultSet = tagStatement.executeQuery();
                while(tagResultSet.next()){
                    tags.add(new Tag(tagResultSet.getInt(1),tagResultSet.getString(2)));
                }
                commentStatement = c.prepareStatement("select id,author,text,creation_date,like_count from comments where event_id=?");
                commentStatement.setInt(1, resultSet.getInt(1));
                commentResultSet = commentStatement.executeQuery();
                while(commentResultSet.next()){
                    comments.add(new Comment(commentResultSet.getInt(1),commentResultSet.getString(2),commentResultSet.getString(3),commentResultSet.getDate(4),commentResultSet.getInt(5)));
                }
                event.setComments(comments);
                event.setTags(tags);
                events.add(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(ps);
            this.closeResultSet(resultSet);
            this.closeConnection(c);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentResultSet);
            this.closeResultSet(resultSet);
        }
        return events;
    }

    @Override
    public List<Event> getAllEvents(int limit, int offset) {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement tagStatement = null;
        ResultSet tagResultSet = null;

        PreparedStatement commentStatement = null;
        ResultSet commentResultSet = null;

        try{
            connection = this.newConnection();
            statement = connection.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description,events.rsvp_count, events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id  order by creation_date desc limit ? offset ? ");
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            resultSet = statement.executeQuery();

            tagStatement = connection.prepareStatement("SELECT tag_entity.id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");
            while(resultSet.next()){
                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagStatement.setInt(1, resultSet.getInt(1));
                tagResultSet = tagStatement.executeQuery();
                while(tagResultSet.next()){
                    tags.add(new Tag(tagResultSet.getInt(1),tagResultSet.getString(2)));
                }
                commentStatement = connection.prepareStatement("select id,author,text,creation_date,like_count from comments where event_id=?");
                commentStatement.setInt(1, resultSet.getInt(1));
                commentResultSet = commentStatement.executeQuery();
                while(commentResultSet.next()){
                    comments.add(new Comment(commentResultSet.getInt(1),commentResultSet.getString(2),commentResultSet.getString(3),commentResultSet.getDate(4),commentResultSet.getInt(5)));
                }
                Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));
                event.setComments(comments);
                event.setTags(tags);
                events.add(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentResultSet);
            this.closeResultSet(resultSet);

        }
        return events;
    }


    @Override
    public List<Event> getEventsSortedByViews(int limit) {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        PreparedStatement tagStatement = null;
        ResultSet tagRes = null;
        PreparedStatement commentStatement = null;
        ResultSet commentRes = null;

        try{
            connection = this.newConnection();
            statement = connection.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description, events.rsvp_count,events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id where events.creation_date>= now()-interval 30 day order by view_count desc limit ?");
            statement.setInt(1, limit);
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                tagStatement = connection.prepareStatement("SELECT id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");                tagStatement.setInt(1, resultSet.getInt(1));
                commentStatement = connection.prepareStatement("select id, author, text, creation_date, like_count from comments where event_id=?");
                commentStatement.setInt(1, resultSet.getInt(1));

                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagRes = tagStatement.executeQuery();
                while(tagRes.next()){
                    tags.add(new Tag(tagRes.getInt(1),tagRes.getString(2)));
                }
                commentRes = commentStatement.executeQuery();
                while(commentRes.next()){
                    comments.add(new Comment(commentRes.getInt(1),commentRes.getString(2),commentRes.getString(3),commentRes.getDate(4),commentRes.getInt(5)));
                }

                Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));
                event.setTags(tags);
                event.setComments(comments);
                events.add(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentRes);
            this.closeResultSet(resultSet);
        }
        return events;
    }

    @Override
    public List<Event> getEventsByKeyword(String keyword,int limit, int offset) {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement tagStatement = null;
        ResultSet tagRes = null;
        PreparedStatement commentStatement = null;
        ResultSet commentRes = null;

        try{
            connection = this.newConnection();
            statement = connection.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description,events.rsvp_count, events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id where events.description like ? or events.title like ? order by creation_date desc ");
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            resultSet = statement.executeQuery();


            while(resultSet.next()){
                tagStatement = connection.prepareStatement("SELECT id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");                tagStatement.setInt(1, resultSet.getInt(1));
                commentStatement = connection.prepareStatement("select id, author, text, creation_date, like_count from comments where event_id=?");
                commentStatement.setInt(1, resultSet.getInt(1));
                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagRes = tagStatement.executeQuery();
                while(tagRes.next()){
                    tags.add(new Tag(tagRes.getInt(1),tagRes.getString(2)));
                }
                commentRes = commentStatement.executeQuery();
                while(commentRes.next()){
                    comments.add(new Comment(commentRes.getInt(1),commentRes.getString(2),commentRes.getString(3),commentRes.getDate(4),commentRes.getInt(5)));
                }
                Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));

                event.setTags(tags);
                event.setComments(comments);

                events.add(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentRes);
            this.closeResultSet(resultSet);
        }
        return events;
    }

    public String commentReaction(ReactionDto c, Integer userId, String visitorId){
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement check = null;
        ResultSet checkRes = null;
        ResultSet resultSet = null;
        PreparedStatement update = null;
        ResultSet updateRes = null;

        try{
            connection = this.newConnection();
            if(userId != null){
                check = connection.prepareStatement("select * from comment_reactions where user_id=? and comment_id=?");
                check.setInt(1, userId);
            }else{
                check = connection.prepareStatement("select * from comment_reactions where  visitor_id=? and comment_id=?");
                check.setString(1, visitorId);
            }
            check.setInt(2,c.getComment_id());

            checkRes = check.executeQuery();
            if(checkRes.next()){  //-1
                return "already thre";
            }
            if(userId != null){
                statement = connection.prepareStatement("insert  into comment_reactions (user_id,comment_id,reaction) values(?,?,?)");
                statement.setInt(1, userId);
                statement.setInt(2, c.getComment_id());
                statement.setInt(3, c.getReaction());
            }
            else{
                statement = connection.prepareStatement("insert  into comment_reactions (visitor_id,comment_id,reaction) values(?,?,?)");
                statement.setString(1, visitorId);
                statement.setInt(2, c.getComment_id());
                statement.setInt(3, c.getReaction());
            }
            statement.executeUpdate();
            update = connection.prepareStatement("update comments set like_count=comments.like_count+? where id=?");
            update.setInt(1,c.getReaction());
            update.setInt(2,c.getComment_id());

            update.executeUpdate();

            return "Like count changed";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(check);
            this.closeStatement(update);
            this.closeResultSet(updateRes);
            this.closeResultSet(checkRes);
        }

    }

    @Override
    public Event rsvpToEvent(int eventId, Integer userId, String visitorId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement rsvpPs = null;
        ResultSet rsvpRes = null;
        PreparedStatement update = null;
        PreparedStatement jesus = null; //provera limita
        ResultSet jesusRes = null;
        PreparedStatement exists = null;
        ResultSet existsRes = null;
        try {
            System.out.println("userId = " + userId);
            System.out.println("visitorId = " + visitorId);
            connection = this.newConnection();
            exists = connection.prepareStatement("select * from events where id=?");
            exists.setInt(1, eventId);
            existsRes = exists.executeQuery();
            if(!existsRes.next()){
                return null;
            }
            if(userId != null){
                statement = connection.prepareStatement("select * from rsvps where user_id=? and event_id=?");
                statement.setInt(1, userId);
                statement.setInt(2, eventId);
            }
            else{
                statement = connection.prepareStatement("select * from rsvps where visitor_id=? and event_id=?");
                statement.setString(1, visitorId);
                statement.setInt(2, eventId);
            }
            resultSet = statement.executeQuery();
            if(!resultSet.next()){
                jesus = connection.prepareStatement("select capacity-rsvp_count from events where id=?");
                jesus.setInt(1, eventId);
                jesusRes = jesus.executeQuery();
                if(jesusRes.next()){
                    if(jesusRes.getInt(1)<0){
                        return null;
                    }
                }
                if(userId!=null){
                    rsvpPs = connection.prepareStatement("insert into rsvps (event_id, user_id) VALUES (?,?)");
                    rsvpPs.setInt(1,eventId);
                    rsvpPs.setInt(2,userId);
                }else{
                    rsvpPs = connection.prepareStatement("insert into rsvps (event_id, visitor_id) VALUES (?,?)");
                    rsvpPs.setInt(1,eventId);
                    rsvpPs.setString(2,visitorId);
                }

                rsvpPs.executeUpdate();
                update = connection.prepareStatement("update events set rsvp_count=events.rsvp_count+1 where id=?");
                update.setInt(1,eventId);
                int affectedRows = update.executeUpdate();
                System.out.println("Rows updated in events table: " + affectedRows);
                return getEventById(eventId, null,null);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(update);
            this.closeResultSet(resultSet);
            this.closeStatement(rsvpPs);
            this.closeResultSet(rsvpRes);

        }
        return null;
    }

    @Override
    public List<Event> filterByCategory(int id, int limit, int offset) {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement tagStatement = null;
        ResultSet tagResultSet = null;

        PreparedStatement commentStatement = null;
        ResultSet commentResultSet = null;

        try{
            connection = this.newConnection();
            statement = connection.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description,events.rsvp_count,events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id where events.category_id=? order by creation_date desc limit ? offset ?");
            statement.setInt(1, id);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            resultSet = statement.executeQuery();

            tagStatement = connection.prepareStatement("SELECT id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");
            while(resultSet.next()){
                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagStatement.setInt(1, resultSet.getInt(1));
                tagResultSet = tagStatement.executeQuery();
                while(tagResultSet.next()){
                    tags.add(new Tag(tagResultSet.getInt(1),tagResultSet.getString(2)));
                }
                commentStatement = connection.prepareStatement("select id,author,text,creation_date,like_count from comments where event_id=?");
                commentStatement.setInt(1, resultSet.getInt(1));
                commentResultSet = commentStatement.executeQuery();
                while(commentResultSet.next()){
                    comments.add(new Comment(commentResultSet.getInt(1),commentResultSet.getString(2),commentResultSet.getString(3),commentResultSet.getDate(4),commentResultSet.getInt(5)));
                }
                Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));
                event.setComments(comments);
                event.setTags(tags);
                events.add(event);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentResultSet);
            this.closeResultSet(resultSet);
        }
        return events;
    }

    @Override
    public List<Event> getEventsByTag(int tagId, int limit, int offset) {
        Connection connection = null;
        PreparedStatement statement = null; //vraca sve eventove za tag
        ResultSet resultSet = null;

        PreparedStatement tagStatement = null;
        ResultSet tagResultSet = null;

        PreparedStatement tagStatement2 = null;
        ResultSet tagResultSet2 = null;

        PreparedStatement commentStatement = null;
        ResultSet commentResultSet = null;

        List<Event> events = new ArrayList<>();

        try{
            connection = this.newConnection();
            tagStatement = connection.prepareStatement("select event_id from tags where tag_id=?");
            tagStatement.setInt(1, tagId);
            tagResultSet = tagStatement.executeQuery();
            while(tagResultSet.next()){
                statement = connection.prepareStatement("SELECT events.id,title,events.description,creation_date,event_date,location,view_count,capacity, users.id,email, users.name, last_name,category_id,  categories.name, categories.description,events.rsvp_count,events.reaction FROM events join users on users.id=events.author_id join categories on category_id=categories.id where events.id=? order by creation_date desc limit ? offset ? ");
                statement.setInt(1, tagResultSet.getInt(1));
                statement.setInt(2, limit);
                statement.setInt(3, offset);
                resultSet = statement.executeQuery();
                tagStatement2 = connection.prepareStatement("SELECT tag_entity.id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");
                while(resultSet.next()){
                    List<Tag> tags = new ArrayList<>();
                    List<Comment> comments = new ArrayList<>();
                    tagStatement2.setInt(1, resultSet.getInt(1));
                    tagResultSet2 = tagStatement2.executeQuery();
                    while(tagResultSet2.next()){
                        tags.add(new Tag(tagResultSet2.getInt(1),tagResultSet2.getString(2)));
                    }
                    commentStatement = connection.prepareStatement("select id,author,text,creation_date,like_count from comments where event_id=?");
                    commentStatement.setInt(1, resultSet.getInt(1));
                    commentResultSet = commentStatement.executeQuery();
                    while(commentResultSet.next()){
                        comments.add(new Comment(commentResultSet.getInt(1),commentResultSet.getString(2),commentResultSet.getString(3),commentResultSet.getDate(4),commentResultSet.getInt(5)));
                    }
                    Event event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                            resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                    event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                    event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));
                    event.setComments(comments);
                    event.setTags(tags);
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(tagStatement);
            this.closeResultSet(resultSet);
            this.closeResultSet(tagResultSet);
            this.closeStatement(tagStatement2);
            this.closeResultSet(tagResultSet2);
            this.closeStatement(commentStatement);
            this.closeResultSet(commentResultSet);
        }

        return events;
    }

    @Override
    public Event getEventById(int id, Integer userId, String visitor) {
        Event event = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement viewCheck = null;
        ResultSet viewRes = null;
        PreparedStatement insertView = null;
        PreparedStatement updateView = null;
        PreparedStatement tagStatement = null;
        ResultSet tagRes = null;
        PreparedStatement commentStatement = null;
        ResultSet commentRes = null;

        try {
            connection = this.newConnection();
            if(userId!= null){
                viewCheck = connection.prepareStatement("SELECT * from event_views where user_id=? and event_id=?");
                viewCheck.setInt(1, userId);
                viewCheck.setInt(2, id);
            }else{
                viewCheck = connection.prepareStatement("SELECT * from event_views where visitor_id=? and event_id=?");
                viewCheck.setString(1, visitor);
                viewCheck.setInt(2, id);
            }
            viewRes = viewCheck.executeQuery();
            if(!viewRes.next()){
                insertView = connection.prepareStatement("INSERT INTO event_views (event_id, user_id, visitor_id) VALUES (?, ?, ?)");
                insertView.setInt(1, id);
                if(userId != null){
                    insertView.setInt(2, userId);
                    insertView.setNull(3, Types.VARCHAR);
                }else {
                    insertView.setNull(2, Types.INTEGER);
                    insertView.setString(3, visitor);
                }
                insertView.executeUpdate();
                updateView = connection.prepareStatement("UPDATE events SET view_count=view_count+1 WHERE id=?");
                updateView.setInt(1, id);
                updateView.executeUpdate();
            }
            statement = connection.prepareStatement("SELECT events.id, events.title, events.description, creation_date, event_date, location," +
                    " view_count, capacity, author_id,email,u.name,last_name, category_id, categories.name,categories.description,events.rsvp_count,events.reaction FROM events join event_booker.categories on events.category_id=categories.id join event_booker.users u on events.author_id = u.id WHERE events.id = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            tagStatement = connection.prepareStatement("SELECT id,tag_entity.keyword from tags join event_booker.tag_entity on tags.tag_id=tag_entity.id where event_id=?");
            tagStatement.setInt(1, id);
            commentStatement = connection.prepareStatement("select id, author, text, creation_date, like_count from comments where event_id=?");
            commentStatement.setInt(1, id);
            if(resultSet.next()){
                List<Tag> tags = new ArrayList<>();
                List<Comment> comments = new ArrayList<>();
                tagRes = tagStatement.executeQuery();
                while(tagRes.next()){
                    tags.add(new Tag(tagRes.getInt(1),tagRes.getString(2)));
                }
                commentRes = commentStatement.executeQuery();
                while(commentRes.next()){
                    comments.add(new Comment(commentRes.getInt(1),commentRes.getString(2),commentRes.getString(3),commentRes.getDate(4),commentRes.getInt(5)));
                }
                event = new Event(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDate(4),
                        resultSet.getDate(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getInt(8),resultSet.getInt(16),resultSet.getInt(17));
                event.setAuthor(new User(resultSet.getInt(9),resultSet.getString(10),resultSet.getString(11),resultSet.getString(12)));
                event.setCategory(new Category(resultSet.getInt(13),resultSet.getString(14),resultSet.getString(15)));
                event.setTags(tags);
                event.setComments(comments);
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeResultSet(viewRes);
            this.closeResultSet(resultSet);
            this.closeStatement(statement);
            this.closeStatement(viewCheck);
            this.closeStatement(insertView);
            this.closeStatement(updateView);
            this.closeConnection(connection);
            this.closeStatement(commentStatement);
            this.closeStatement(tagStatement);
            this.closeResultSet(commentRes);
            this.closeResultSet(resultSet);
        }
        return event;
    }

    @Override
    public EventDto createEvent(EventDto e) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String[] generated = {"id"};
            statement = connection.prepareStatement("INSERT INTO events (title, event_date, location, author_id, description, category_id, capacity, view_count) VALUES (?,?,?,?,?,?,?,?)",generated);
            statement.setString(1,e.getTitle());
            statement.setDate(2,  Date.valueOf(e.getEvent_date()));
            statement.setString(3,e.getLocation());
            statement.setInt(4,e.getAuthor_id());
            statement.setString(5,e.getDescription());
            statement.setInt(6,e.getCategory_id());
            statement.setInt(7,e.getMax_capacity());
            statement.setInt(8,0);
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();

            if(resultSet.next()){
                e.setId(resultSet.getInt(1));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return e;
    }

    @Override
    public EventDto editEvent(EventDto event) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            statement = connection.prepareStatement("UPDATE events set title=?, event_date=?, location=?, author_id=?, description=?, category_id=?, capacity=? where id=?");
            statement.setString(1,event.getTitle());
            statement.setDate(2, Date.valueOf(event.getEvent_date()));
            statement.setString(3,event.getLocation());
            statement.setInt(4,event.getAuthor_id());
            statement.setString(5,event.getDescription());
            statement.setInt(6,event.getCategory_id());
            statement.setInt(7,event.getMax_capacity());
            statement.setInt(8,event.getId());

            statement.executeUpdate();
            return event;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeConnection(connection);
        }
    }

    @Override
    public void deleteEvent(int id) { //izbrisan event i svi njegovi komentari
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement commentStatement = null;

        try {
            connection = this.newConnection();
            statement = connection.prepareStatement("DELETE FROM events WHERE id=?");
            statement.setInt(1,id);
            statement.executeUpdate();
            commentStatement = connection.prepareStatement("DELETE FROM comments WHERE id=?");
            commentStatement.setInt(1,id);
            commentStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(statement);
            this.closeStatement(commentStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public Event addComment(CommentDto comment) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            String[] generated = {"id"};
            statement = connection.prepareStatement("INSERT INTO comments (author, text, event_id, like_count) values (?,?,?,?)" ,generated);
            statement = connection.prepareStatement("INSERT INTO comments (author, text, event_id, like_count) values (?,?,?,?)" ,generated);
            statement.setString(1,comment.getAuthor());
            statement.setString(2,comment.getText());
            statement.setInt(3,comment.getEvent_id());
            statement.setInt(4,comment.getLike_count());

            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                comment.setId(resultSet.getInt(1));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }finally{
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return getEventById(comment.getEvent_id(),null,null);
    }


    @Override
    public Event addTag(TagDto tag) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement checkStatement = null;
        PreparedStatement exists = null;
        ResultSet existsResult = null;
        PreparedStatement update = null;
        ResultSet updateResult = null;

        try{
            String[] generated = {"id"};
            connection = this.newConnection();
            exists = connection.prepareStatement("SELECT id FROM tag_entity WHERE keyword=?");
            exists.setString(1,tag.getKeyword());
            existsResult = exists.executeQuery();
            int tagEntityId;
            if(existsResult.next()){ //da li postoji tag vec u bazi
                tagEntityId = existsResult.getInt(1);
                checkStatement = connection.prepareStatement("select * from tags where event_id=? and tag_id=?");
                checkStatement.setInt(1,tag.getEvent_id());
                checkStatement.setInt(2,tagEntityId);
                resultSet = checkStatement.executeQuery();
                if(resultSet.next()){ //da li postoji kombinacija tag-event
                    return null; //ako postoji vec za taj event taj tag
                }else {
                    statement = connection.prepareStatement("INSERT INTO tags (tag_id, event_id) values (?,?)");
                    statement.setInt(1,tagEntityId);
                    statement.setInt(2,tag.getEvent_id());
                    statement.executeUpdate();

                }
            }
            else {
                update = connection.prepareStatement("insert into tag_entity (keyword) values (?)",Statement.RETURN_GENERATED_KEYS);

                update.setString(1,tag.getKeyword());
                update.executeUpdate();
                ResultSet generatedKeys = update.getGeneratedKeys();
                if(generatedKeys.next()){
                    tagEntityId = generatedKeys.getInt(1);
                }else {
                    throw new SQLException("Failed to insert tag_entity");
                }

                statement = connection.prepareStatement("INSERT INTO tags (tag_id, event_id) values (?,?)");
                statement.setInt(1,tagEntityId);
                statement.setInt(2,tag.getEvent_id());
                statement.executeUpdate();

            }
            tag.setId(tagEntityId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
            this.closeStatement(checkStatement);
        }
        return getEventById(tag.getEvent_id(),null,null);
    }
}
