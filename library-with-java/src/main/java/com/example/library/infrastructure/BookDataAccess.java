package com.example.library.infrastructure;

import com.example.library.domain.RentedBook;

import java.util.List;
import java.util.Optional;

public interface BookDataAccess {
    void addBook(RentedBook book);

    Optional<RentedBook> findBookByIsbn(String isbn);

    List<RentedBook> findAllBooks();

    int countBooks();

    boolean updateBookStatus(String isbn, RentedBook.BookStatus newStatus);

    boolean removeBookByIsbn(String isbn);
}
