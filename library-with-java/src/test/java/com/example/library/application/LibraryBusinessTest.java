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
        Book book = new Book("CS01", "토비의 스프링", "이일민", 100);
        RentedBook rentedBook = new RentedBook(book);

        memoryConcurrentDataAccess.addBook(rentedBook);

        libraryBusiness.rentBook(rentedBook.getIsbn());

        Optional<RentedBook> subject = libraryBusiness.findBookWithSubject("토비의 스프링");

        assertThat(subject)
                .isPresent()
                .hasValueSatisfying(rentedBook1 -> assertThat(rentedBook1.isRented()).isTrue());
    }

    @Test
    void 도서_상태_변경_() {
    }

    @Test
    void 도서_상태_변경_분실() {
        Book book = new Book("CS01", "토비의 스프링", "이일민", 100);

        memoryConcurrentDataAccess.addBook(new RentedBook(book, RentedBook.BookStatus.RENTED));

        libraryBusiness.lostBook(book.getIsbn());

        Optional<RentedBook> subject = libraryBusiness.findBookWithSubject("토비의 스프링");

        assertThat(book).isNotNull();
        assertThat(subject)
                .isPresent()
                .hasValueSatisfying(rentedBook -> assertThat(rentedBook.isLost()).isTrue());
    }

}