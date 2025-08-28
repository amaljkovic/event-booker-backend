package rs.raf.repositories;

import java.sql.*;
import java.util.Optional;

public abstract class AbstractRepository {
    protected Connection newConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://"+this.getHost()+":"+this.getPort()+"/"+this.getDatabaseName(),this.getUsername(),this.getPassword());
    }

    protected String getHost(){
        return "localhost";
    }
    protected int getPort(){
        return 3306;
    }
    protected String getDatabaseName(){
        return "event_booker";
    }
    protected String getUsername(){
        return "root";
    }
    protected String getPassword(){
        return "root";
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                Optional.of(statement).get().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    protected void closeResultSet(ResultSet resultSet) {
        if(resultSet != null){
            try {
                Optional.of(resultSet).get().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    protected void closeConnection(Connection connection) {
        if(connection != null){
            try {
                Optional.of(connection).get().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
}
