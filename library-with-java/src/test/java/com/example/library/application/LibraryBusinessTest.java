package com.example.library.application;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import com.example.library.infrastructure.MemoryConcurrentDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibraryBusinessTest {

    private LibraryBusiness libraryBusiness;
    private MemoryConcurrentDataAccess memoryConcurrentDataAccess;
    @BeforeEach
    void setUp() {
        memoryConcurrentDataAccess = new MemoryConcurrentDataAccess(null);
        libraryBusiness = new LibraryBusiness(memoryConcurrentDataAccess);
    }

    @Test
    void 도서_상태변경_대여() {
        RentedBook rentedBook = makeRentBook("CS001", RentedBook.BookStatus.AVAILABLE);

        memoryConcurrentDataAccess.addBook(rentedBook);

        libraryBusiness.rentBook(rentedBook.getIsbn());

        Optional<RentedBook> subject = libraryBusiness.findBookWithSubject("토비의 스프링");

        assertThat(subject)
                .isPresent()
                .hasValueSatisfying(rentedBook1 -> assertThat(rentedBook1.isRented()).isTrue());
    }

    @Test
    void 도서_상태_변경_분실() {

        RentedBook rentBook = makeRentBook("CS001", RentedBook.BookStatus.RENTED);
        memoryConcurrentDataAccess.addBook(rentBook);

        libraryBusiness.lostBook(rentBook.getIsbn());

        Optional<RentedBook> subject = libraryBusiness.findBookWithSubject("토비의 스프링");

        assertThat(subject)
                .isPresent()
                .hasValueSatisfying(rentedBook -> assertThat(rentedBook.isLost()).isTrue());
    }

    @Test
    void 도서_상태_변경_반납() {
        RentedBook rentedBook = makeRentBook("CS0001", RentedBook.BookStatus.RENTED);
        memoryConcurrentDataAccess.addBook(rentedBook);

        libraryBusiness.returnBook(rentedBook.getIsbn());

        Optional<RentedBook> subject = libraryBusiness.findBookWithSubject(rentedBook.getBook().getSubject());

        assertThat(subject)
                .isPresent()
                .hasValueSatisfying(rentedBook1 -> assertThat(rentedBook1.isReturned()).isTrue());
    }

    private static RentedBook makeRentBook(String isbn, RentedBook.BookStatus status) {
        Book book = new Book(isbn, "토비의 스프링", "이일민", 100);
        return new RentedBook(book, status);
    }
}