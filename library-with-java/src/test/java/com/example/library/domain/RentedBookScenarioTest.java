package com.example.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RentedBookScenarioTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("CS0001", "Clean Code", "로버트 C. 마틴", 200);
    }

    @Test
    void 도서_생성_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);

        // when
        assertThat(rentedBook.getReturnedTime()).isNull();
        // then
        assertThat(rentedBook.isAvailable()).isTrue();
    }

    @Test
    void 도서_렌트_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);

        // when
        rentedBook.returnBook();

        // then
        assertThat(rentedBook.isAvailable()).isFalse();
        assertThat(rentedBook.isRented()).isTrue();
    }

    @Test
    void 도서_정리중_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);
        rentedBook.returnBook();

        // when
        rentedBook.organize();

        // then
        assertThat(rentedBook.isOrganized()).isTrue();
    }

    @Test
    void 도서_분실_변경_테스트() {
        // given
        RentedBook rentedBook = new RentedBook(book);
        rentedBook.returnBook();

        // when
        rentedBook.lost();

        // then
        assertThat(rentedBook.isLost()).isTrue();
    }
}