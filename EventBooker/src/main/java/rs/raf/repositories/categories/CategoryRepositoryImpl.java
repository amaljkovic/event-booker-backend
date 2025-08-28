package rs.raf.repositories.categories;

import rs.raf.dto.CategoryDto;
import rs.raf.entities.Category;
import rs.raf.repositories.AbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl extends AbstractRepository implements CategoryRepository {
    @Override
    public List<Category> getCategories(int size, int offset) {
        Connection connection = null;
        List<Category> categories = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM categories order by name desc limit ? offset ?");
            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, offset);
            resultSet = preparedStatement.executeQuery();


            while(resultSet.next()){
                Category category = new Category(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
            this.closeResultSet(resultSet);
        }
        return categories;
    }

    @Override
    public Category addCategory(Category category) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = this.newConnection();
            String[] generated = {"id"};
            preparedStatement = connection.prepareStatement("INSERT INTO categories (name,description) VALUES (?,?)",generated);
            preparedStatement.setString(1,category.getName());
            preparedStatement.setString(2,category.getDescription());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if(resultSet.next()){
                category.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
            this.closeResultSet(resultSet);
        }
        return category;
    }

    @Override
    public Category editCategory(Category category) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("UPDATE categories SET name=?,description=? WHERE id=?");
            preparedStatement.setString(1,category.getName());
            preparedStatement.setString(2,category.getDescription());
            preparedStatement.setInt(3,category.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
            this.closeResultSet(resultSet);
        }
        return category;
    }

    @Override
    public Category getCategoryBy(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM categories where id=?");
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return new Category(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
            this.closeResultSet(resultSet);
        }
        return null;
    }

    @Override
    public String deleteCategory(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement eventCategoryCheck = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();
            eventCategoryCheck = connection.prepareStatement("SELECT * FROM events WHERE events.category_id = ?");
            eventCategoryCheck.setInt(1, id);
            resultSet = eventCategoryCheck.executeQuery();
             if(resultSet.next()){
                 System.out.println("Can't delete category that is used in event");
                 return "Could not delete category";
             }
             else {
                 preparedStatement = connection.prepareStatement("DELETE FROM categories WHERE id = ?");
                 preparedStatement.setInt(1, id);
                 preparedStatement.executeUpdate();
                 return "Category deleted";

             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
            this.closeResultSet(resultSet);
        }
    }

    @Override
    public int countCategories() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("select count(*) from categories");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return 0;
    }
}
