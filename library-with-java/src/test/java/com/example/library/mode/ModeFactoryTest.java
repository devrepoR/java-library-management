package com.example.library.mode;

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
class ModeFactoryTest {

    private static final String TEST_CSV = "data/library-test.csv";

    @Test
    void 테스트모드_테스트() {
        ModeFactory testMode = ModeFactory.TEST_MODE;
        assertThat(testMode.mode(null)).isInstanceOf(TestMode.class);
    }

    @Test
    void 운영모드_테스트() {
        ModeFactory realMode = ModeFactory.REAL_MODE;
        assertThat(realMode.mode(TEST_CSV)).isInstanceOf(RealMode.class);
    }

    private static Stream<Arguments> modeList() {
        return Stream.of(
                Arguments.of(ModeFactory.TEST_MODE, null, TestMode.class),
                Arguments.of(ModeFactory.REAL_MODE, TEST_CSV, RealMode.class)
        );
    }

    @MethodSource("modeList")
    @ParameterizedTest(name = "{0} 모드는 {1} 클래스를 사용합니다.")
    void 모드별_테스트(ModeFactory mode, String test, Class<?> clazz) {
        // given

        // when
        ModePolicy policy = ModeFactory.of(mode, test);

        // then
        assertThat(policy).isInstanceOf(clazz);
    }
}