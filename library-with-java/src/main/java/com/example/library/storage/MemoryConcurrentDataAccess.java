package com.example.library.storage;

import com.example.library.application.RentedBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryConcurrentDataAccess implements BookDataAccess {

    private final Map<String, RentedBook> books = new ConcurrentHashMap<>();

    public MemoryConcurrentDataAccess(String path) {
    }

    public void addBook(RentedBook book) {
        books.put(book.getIsbn(), book);
    }

    public Optional<RentedBook> findBookByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    public List<RentedBook> findAllBooks() {
        return new ArrayList<>(books.values());
    }

    @Override
    public int countBooks() {
        return books.size();
    }

    public boolean updateBookStatus(String isbn, RentedBook.BookStatus newStatus) {
        RentedBook book = books.get(isbn);
        if (book != null) {
            book.updateStatus(newStatus);
            return true;
        }
        return false;
    }

    public boolean removeBookByIsbn(String isbn) {
        return books.remove(isbn) != null;
    }
}
