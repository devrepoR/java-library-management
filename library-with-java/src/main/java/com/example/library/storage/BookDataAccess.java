package com.example.library.storage;

import com.example.library.application.Book;

import java.util.List;
import java.util.Optional;

public interface BookDataAccess {
    void addBook(Book book);

    Optional<Book> findBookByIsbn(String isbn);

    List<Book> findAllBooks();

    int countBooks();

    boolean updateBookStatus(String isbn, Book.BookStatus newStatus);

    boolean removeBookByIsbn(String isbn);
}
