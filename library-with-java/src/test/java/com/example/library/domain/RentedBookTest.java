package com.example.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RentedBookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "500"
        );
    }

    @Test
    void 도서_상태_대여중_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);
        assertThat(rentedBook.isAvailable()).isTrue();

        // when
        rentedBook.returnBook();

        // then
        assertThat(rentedBook.isRented()).isTrue();
    }

    @Test
    void 도서_상태_도서정리중_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);

        // when
        rentedBook.returnBook();

        // then
        assertThat(rentedBook.isRented()).isTrue();

        // when
        rentedBook.organize();

        // then
        assertThat(rentedBook.isOrganized()).isTrue();

        // when
        rentedBook.available();

        // then
        assertThat(rentedBook.isAvailable()).isTrue();
    }

    @Test
    void 도서_상태_분실됨_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);

        // when
        rentedBook.returnBook();

        // then
        assertThat(rentedBook.isRented()).isTrue();

        // when
        rentedBook.lost();

        // then
        assertThat(rentedBook.isLost()).isTrue();
    }
}