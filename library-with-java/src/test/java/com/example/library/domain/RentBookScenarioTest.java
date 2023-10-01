package com.example.library.domain;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("도서 상태 변경 시나리오 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RentBookScenarioTest {

    private RentedBook rentedBook;

    @Order(1)
    @Test
    void 도서_생성_테스트() {
        // given
        Book book = new Book(
                "CS0001"
                , "Clean Code"
                , "로버트 C. 마틴"
                , 5000
        );
        rentedBook = new RentedBook(book);

        // then
        assertThat(rentedBook.isAvailable()).isTrue();
    }

    @Order(2)
    @Test
    void 도서_대출중_테스트() {

        // when
        rentedBook.returnBook();

        // then
        assertThat(rentedBook.isRented()).isTrue();
    }

    @Order(3)
    @Test
    void 도서_도서_정리중_테스트() {

        // when
        rentedBook.organize();

        // then
        assertThat(rentedBook.getStatus()).isEqualTo(RentedBook.BookStatus.ORGANIZING);
    }

    @Order(4)
    @Test
    void 도서_대여_가능_테스트() {

        // when
        rentedBook.updateStatus(RentedBook.BookStatus.AVAILABLE);

        // then
        assertThat(rentedBook.getStatus()).isEqualTo(RentedBook.BookStatus.AVAILABLE);
    }
}