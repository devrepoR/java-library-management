package com.example.library.application;

import com.example.library.domain.RentedBook;

import java.util.List;
import java.util.Optional;

public interface LibraryInterface {
    RentedBook regist(RentedBook book);

    List<RentedBook> books();

    Optional<RentedBook> findBookWithSubject(String subject);

    RentedBook rentBook(String isbn);

    void returnBook(String isbn);

    void lostBook(String isbn);

    boolean deleteBook(String isbn);
}
