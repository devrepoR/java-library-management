package com.example.library.actions;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import com.example.library.application.LibraryInterface;
import com.example.library.application.LibraryBusiness;
import com.example.library.infrastructure.MemoryConcurrentDataAccess;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibraryActionsTest {

    private static Stream<Arguments> argument() {
        return Stream.of(
            Arguments.of("REGISTER_BOOK", new RentedBook(new Book("CS0001", "Clean Code", "Robert C. Martin", 100))),
            Arguments.of("FIND_ALL_BOOK", null),
            Arguments.of("FIND_BOOK_BY_SUBJECT", "Clean Code")
        );
    }

    private static Stream<Arguments> argumentNotFound() {
        return Stream.of(
                Arguments.of("RENT_BOOK", "CS0001"),
                Arguments.of("RETURN_BOOK", "CS0001"),
                Arguments.of("LOST_BOOK", "CS0001"),
                Arguments.of("DELETE_BOOK", "CS0001")
        );
    }

    @MethodSource("argument")
    @ParameterizedTest(name = "메뉴에 따른 메서드 호출 - {0}, {1}")
    void 메뉴에_따른메서드_호출(String methodType, Object param) {
        // Given
        LibraryInterface libraryService = new LibraryBusiness(new MemoryConcurrentDataAccess(null));

        LibraryActions registerBookAction = LibraryActions.findByName(methodType).get();
        registerBookAction.perform(libraryService, param);

    }

    @Test
    void 도서_등록_테스트() {
        // Given
        LibraryInterface libraryService = new LibraryBusiness(new MemoryConcurrentDataAccess(null));
        Book expected = new Book("CS0001", "Clean Code", "Robert C. Martin", 100);

        // When
        RentedBook rentedBook = LibraryActions.findByName("REGISTER_BOOK")
                .get()
                .perform(libraryService, new RentedBook(expected));

        // Then
        assertThat(rentedBook.getIsbn()).isEqualTo(expected.getIsbn());
        assertThat(rentedBook.getRentedTime()).isNotNull();
        assertThat(rentedBook.getReturnedTime()).isNull();
    }
}