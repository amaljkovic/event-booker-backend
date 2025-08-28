package rs.raf.repositories.categories;

import rs.raf.dto.CategoryDto;
import rs.raf.entities.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getCategories(int size, int offset);
    Category addCategory(Category category);
    Category editCategory(Category category);
    Category getCategoryBy(int id);
    String deleteCategory(int id); //nije dozvoljeno ako ima eventa u toj kategoriji
    int countCategories();
}
