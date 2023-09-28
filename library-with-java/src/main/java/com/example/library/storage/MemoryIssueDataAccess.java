package com.example.library.storage;

import com.example.library.application.Book;

import java.util.*;

public class MemoryIssueDataAccess implements BookDataAccess {

    private final Map<String, Book> books = new HashMap<>();

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    public List<Book> findAllBooks() {
        return new ArrayList<>(books.values());
    }

    @Override
    public int countBooks() {
        return books.size();
    }

    public boolean updateBookStatus(String isbn, Book.BookStatus newStatus) {
        Book book = books.get(isbn);
        if (book != null) {
            book.changeStatus(newStatus);
            return true;
        }
        return false;
    }

    public boolean removeBookByIsbn(String isbn) {
        return books.remove(isbn) != null;
    }
}
