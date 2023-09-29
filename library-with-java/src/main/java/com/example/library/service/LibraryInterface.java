package com.example.library.service;

import com.example.library.application.RentedBook;

import java.util.List;

public interface LibraryInterface {
    RentedBook regist(RentedBook book);

    List<RentedBook> books();

    RentedBook findBookWithSubject(String subject);

    RentedBook rentBook(String isbn);

    void returnBook(String isbn);

    void lostBook(String isbn);

    boolean deleteBook(String isbn);
}
