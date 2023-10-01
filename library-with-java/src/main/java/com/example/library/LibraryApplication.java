package com.example.library;

import com.example.library.batch.BookStatusUpdateBatch;
import com.example.library.infrastructure.BookDataAccess;
import com.example.library.infrastructure.FileChannelDataAccess;

public class LibraryApplication {

    public static final String FILE_PATH = "/Users/seok/IdeaProjects/programmers/java-library-management/library-with-java/data/library-test.csv";

    public static void main(String[] args) {

        BookDataAccess dataAccess = new FileChannelDataAccess(FILE_PATH); // 실제 데이터 액세스 객체로 대체
        final BookStatusUpdateBatch batch = new BookStatusUpdateBatch(dataAccess);

        Runtime.getRuntime().addShutdownHook(new Thread(batch::shutdown));
    }
}
