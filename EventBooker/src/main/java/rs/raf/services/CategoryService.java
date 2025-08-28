package rs.raf.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.raf.dto.Page;
import rs.raf.entities.Category;
import rs.raf.repositories.categories.CategoryRepository;
import rs.raf.repositories.categories.CategoryRepositoryImpl;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private int qInt(Request req, String name, int def) {
        try { return Integer.parseInt(req.queryParams(name)); } catch (Exception e) { return def; }
    }
    ObjectMapper mapper = new ObjectMapper();
    CategoryRepository categoryRepository = new CategoryRepositoryImpl();

    public Page<Category> getCategories(Request request, Response response) {
        int page = Math.max(1, qInt(request, "page", 1));
        int size = Math.min(50, Math.max(1, qInt(request, "size", 10)));
        int offset = (page - 1) * size;

        List<Category> categories = categoryRepository.getCategories(size,offset);
        int count  = categoryRepository.countCategories();

        return new Page<>(categories, page, size, count);
    }

    public Category getCategoryById(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        return categoryRepository.getCategoryBy(id);
    }

    public Category addCategory(Request request, Response response) throws JsonProcessingException {
        Category newCategory = mapper.readValue(request.body(), Category.class);
        Category created =  categoryRepository.addCategory(newCategory);
        newCategory.setId(created.getId());
        return newCategory;
    }

    public Category editCategory(Request request, Response response) throws JsonProcessingException {
        int id = Integer.parseInt(request.params(":id"));
        Category category = mapper.readValue(request.body(), Category.class);
        category.setId(id);
        return categoryRepository.editCategory(category);
    }

    public String delete(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        return  categoryRepository.deleteCategory(id);

    }
}
