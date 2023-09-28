package com.example.library.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("도서 도메인 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookTest {

    @Test
    void 도서_생성_테스트() {
        Book book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "500"
        );

        assertThat(book.getAuthor()).isEqualTo("로버트 C. 마틴");
    }

    @Test
    void 도서_상태_대여중_변경_테스트() {
        Book book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "500"
        );

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.AVAILABLE);

        book.changeStatus(Book.BookStatus.RENTED);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.RENTED);
    }

    @Test
    void 도서_상태_도서정리중_변경_테스트() {
        Book book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "500"
        );

        book.changeStatus(Book.BookStatus.RENTED);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.RENTED);

        book.changeStatus(Book.BookStatus.ORGANIZING);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.ORGANIZING);

        book.changeStatus(Book.BookStatus.AVAILABLE);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.AVAILABLE);
    }

    @Test
    void 도서_상태_분실됨_변경_테스트() {
        Book book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "500"
        );

        book.changeStatus(Book.BookStatus.RENTED);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.RENTED);

        book.changeStatus(Book.BookStatus.LOST);

        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.LOST);
    }
}