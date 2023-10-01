package com.example.library.utils;

import java.time.format.DateTimeFormatter;

public interface FileConstant {
    String FILE_DELIMITER = ",";
    DateTimeFormatter FORMAT_ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
