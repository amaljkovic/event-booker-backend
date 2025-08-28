package rs.raf.dto;

import java.util.List;

public class Page<T>{
    public List<T> items;
    public int page;        // trenutna strana (1-based)
    public int pageSize;    // veličina strane (npr. 10)
    public int totalItems;  // ukupan broj zapisa
    public int totalPages;  // za UI
    public boolean hasPrev;
    public boolean hasNext;

    public Page(List<T> items, int page, int pageSize, int totalItems) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil(totalItems / (double) pageSize);
        this.hasPrev = page > 1;
        this.hasNext = page < totalPages;
    }
}
