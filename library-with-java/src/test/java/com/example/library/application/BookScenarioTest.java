package com.example.library.application;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("도서 상태 변경 시나리오 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookScenarioTest {

    private Book book;

    @Order(1)
    @Test
    void 도서_생성_테스트() {
        // given
        book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , "5000"
        );

        // then
        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.AVAILABLE);
    }

    @Order(2)
    @Test
    void 도서_대출중_테스트() {

        // when
        book.changeStatus(Book.BookStatus.RENTED);

        // then
        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.RENTED);
    }

    @Order(3)
    @Test
    void 도서_도서_정리중_테스트() {

        // when
        book.changeStatus(Book.BookStatus.ORGANIZING);

        // then
        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.ORGANIZING);
    }

    @Order(4)
    @Test
    void 도서_대여_가능_테스트() {

        // when
        book.changeStatus(Book.BookStatus.AVAILABLE);

        // then
        assertThat(book.getStatus()).isEqualTo(Book.BookStatus.AVAILABLE);
    }
}