package com.example.library.console.mode;

import com.example.library.infrastructure.BookDataAccess;

import java.util.logging.Logger;

public class RealMode implements ModePolicy {
    private static final Logger log = Logger.getLogger(RealMode.class.getName());

    private final BookDataAccess bookDataAccess;

    public RealMode(BookDataAccess bookDataAccess) {
        this.bookDataAccess = bookDataAccess;
    }

    @Override
    public void apply(String number) {
        log.info("Real mode applied for number: " + number);
        switch(number) {
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5" :
                break;
            default:
                throw new IllegalArgumentException("Invalid number: " + number);
        }
    }
}
