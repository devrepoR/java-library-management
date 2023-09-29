package com.example.library.mode;

import com.example.library.storage.BookDataAccess;

import java.util.logging.Logger;

public class TestMode implements ModePolicy {
    private static final Logger log = Logger.getLogger(TestMode.class.getName());
    private final BookDataAccess bookDataAccess;

    public TestMode(BookDataAccess bookDataAccess) {
        this.bookDataAccess = bookDataAccess;
    }

    @Override
    public void apply(String number) {
        log.info("Test mode applied for number: " + number);
    }
}
