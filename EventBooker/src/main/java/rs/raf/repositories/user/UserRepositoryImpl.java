package rs.raf.repositories.user;

import rs.raf.entities.User;
import rs.raf.repositories.AbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserRepositoryImpl extends AbstractRepository implements UserRepository {
    @Override
    public List<User> getUsers(int limit, int offset) {
        List<User> users = new ArrayList<User>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM users LIMIT ? OFFSET ?");
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5),resultSet.getBoolean(6),resultSet.getBoolean(7)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
            return users;
    }

    @Override
    public User createUser(User user) {
        List<User> users = new ArrayList<User>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            int id = new Random().nextInt(Integer.MAX_VALUE);
            connection = this.newConnection();  //todo random id
            String[] generated = {"id"};
            preparedStatement = connection.prepareStatement("INSERT INTO users (email, password, name, last_name, role, active) VALUES ( ?, ?, ?, ?, ?, ?)",generated);
            preparedStatement.setString(1,user.getEmail());
            preparedStatement.setString(2,user.getPassword());
            preparedStatement.setString(3,user.getName());
            preparedStatement.setString(4,user.getLast_name());
            preparedStatement.setBoolean(5,user.isRole());
            preparedStatement.setBoolean(6,user.isActive());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return user;
    }

    @Override
    public User editUser(User user) {
        return null;
    }

    @Override
    public void activateUser(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("select active from users where id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                boolean active = !resultSet.getBoolean(1);
                updateStatement = connection.prepareStatement("update users set active = ? where id = ?");
                updateStatement.setBoolean(1, active);
                updateStatement.setInt(2, id);
                updateStatement.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM users where email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5),resultSet.getBoolean(6),resultSet.getBoolean(7));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return null;
    }

    @Override
    public User getUserById(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM users where id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5),resultSet.getBoolean(6),resultSet.getBoolean(7));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return null;
    }

    @Override
    public int countUsers() {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = this.newConnection();
            ps = connection.prepareStatement("select count(*) from users");
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(ps);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return 0;
    }
}
