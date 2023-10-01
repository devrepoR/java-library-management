package com.example.library.domain;

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
                , 500
        );

        assertThat(book.getAuthor()).isEqualTo("로버트 C. 마틴");
    }
}