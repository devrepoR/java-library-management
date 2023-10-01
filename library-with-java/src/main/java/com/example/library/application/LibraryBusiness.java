package com.example.library.application;

import com.example.library.domain.RentedBook;
import com.example.library.infrastructure.BookDataAccess;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class LibraryBusiness implements LibraryInterface {
    private static final Logger log = Logger.getLogger(LibraryBusiness.class.getName());
    private final BookDataAccess dataAccess;

    public LibraryBusiness(BookDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public RentedBook regist(RentedBook book) {
        log.info("[LOG] [REGIST] [BOOK] [" + book + "]");
        boolean empty = dataAccess.findBookByIsbn(book.getIsbn()).isEmpty();
        if (!empty) {
            throw new RuntimeException("Book is already registered");
        }

        dataAccess.addBook(book);

        return book;
    }

    @Override
    public List<RentedBook> books() {
        return dataAccess.findAllBooks();
    }

    @Override
    public Optional<RentedBook> findBookWithSubject(String subject) {
        // subject로 찾는 메서드가 없으므로, 필요하다면 구현해야 합니다.
        // 예를 들어, findAllBooks 메서드를 사용하여 주어진 subject와 일치하는 책을 찾을 수 있습니다.
        return dataAccess.findAllBooks().stream()
                .filter(book -> subject.equals(book.getBook().getSubject()))
                .findFirst();
    }

    @Override
    public RentedBook rentBook(String isbn) {
        RentedBook rentedBook = findByIsbn(isbn, "Book is not found");

        checkBookRent(rentedBook);

        boolean updated = dataAccess.updateBookStatus(isbn, RentedBook.BookStatus.RENTED);
        if (!updated) {
            throw new RuntimeException("Book is already rented");
        }

        return findByIsbn(isbn, "Book is already rented");
    }

    @Override
    public void returnBook(String isbn) {
        RentedBook rentedBook = findByIsbn(isbn, "Book is not found");

        // 대여중인 책만 반납
        if(!rentedBook.isRented()) {
            throw new RuntimeException("상태가 대여중인 경우에만 반납처리 가능합니다.");
        }
        // 대여중 -> 정리중
        boolean updated = dataAccess.updateBookStatus(isbn, RentedBook.BookStatus.ORGANIZING);
        if (!updated) {
            throw new RuntimeException("Book is already returned");
        }
    }

    @Override
    public void lostBook(String isbn) {
        RentedBook rentedBook = findByIsbn(isbn, "Book is not found");

        RentedBook.BookStatus status = rentedBook.getStatus();
        // 상태가 대여 중이 아니면, 책을 분실할 수 없습니다.
        if (status != RentedBook.BookStatus.RENTED) {
            throw new RuntimeException("상태가 대여중인 경우에만 분실처리 가능합니다.");
        }

        // 대여중 -> 분실
        boolean updated = dataAccess.updateBookStatus(isbn, RentedBook.BookStatus.LOST);
        if (!updated) {
            throw new RuntimeException("Book is already lost");
        }
    }

    @Override
    public boolean deleteBook(String isbn) {
        RentedBook rentedBook = findByIsbn(isbn, "Book is not found");

        if(rentedBook.isRented()) {
            throw new RuntimeException("대여중인 책은 삭제할 수 없습니다.");
        }
        if(rentedBook.isLost()) {
            throw new RuntimeException("분실된 책은 삭제할 수 없습니다.");
        }

        boolean updated = dataAccess.removeBookByIsbn(isbn);
        if (!updated) {
            throw new RuntimeException("Book is already deleted");
        }
        return updated;
    }

    private static void checkBookRent(RentedBook rentedBook) {
        if(rentedBook.isRented()) {
            throw new RuntimeException("대여중인 책은 대여할 수 없습니다.");
        }

        if(rentedBook.isOrganized()) {
            throw new RuntimeException("정리중인 책은 대여할 수 없습니다.");
        }

        if(rentedBook.isLost()) {
            throw new RuntimeException("분실된 책은 대여할 수 없습니다.");
        }
    }

    private RentedBook findByIsbn(String isbn, String message) {
        return dataAccess.findBookByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException(message));
    }
}
