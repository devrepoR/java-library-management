package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.LibraryInterface;

public class LostBookExecutor implements LibraryActionExecutor<String, Void> {
    @Override
    public Void execute(LibraryInterface service, String isbn) {
        service.lostBook(isbn);
        // Perform post-lost operations here, if needed.
        System.out.println("Book reported as lost: " + isbn); // Example to print the lost book's isbn.
        return null;
    }
}
