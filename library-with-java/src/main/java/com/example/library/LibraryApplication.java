package com.example.library;

import com.example.library.batch.BookStatusUpdater;
import com.example.library.storage.FileChannelDataAccess;

public class LibraryApplication {
    public static void main(String[] args) {
        new BookStatusUpdater(new FileChannelDataAccess("/Users/seok/IdeaProjects/programmers/java-library-management/library-with-java/data/library-test.csv"));
    }
}
