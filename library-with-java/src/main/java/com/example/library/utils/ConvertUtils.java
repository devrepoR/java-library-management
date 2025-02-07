package com.example.library.utils;

import java.time.LocalDateTime;
import java.util.function.Function;

import static com.example.library.utils.FileConstant.FORMAT_ISO_DATE;

public class ConvertUtils {

    private ConvertUtils() {}

    public static <T> T convert(String field, Function<String, T> parseFunction) {
        try {
            return parseFunction.apply(field);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 형식이 잘못되었습니다." + field, e);
        }
    }

    public static <S, T> T convert(S field, Function<S, T> parseFunction, String errorMessage) {
        if (field == null) {
            return null; // or return some default value
        }
        try {
            return parseFunction.apply(field);
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return convert(dateTime, dt -> dt.format(FORMAT_ISO_DATE), "날짜 시간 변환에 실패하였습니다.");
    }

    public static LocalDateTime parseLocalDateTime(String dateField) {
        if(dateField == null || dateField.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateField, FORMAT_ISO_DATE);
        } catch (Exception e) {
            throw new RuntimeException("날짜 형식이 잘못되었습니다." + dateField, e);
        }
    }
}
