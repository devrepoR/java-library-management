package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.service.LibraryInterface;

public class ReturnBookExecutor implements LibraryActionExecutor<String, Void> {
    @Override
    public Void execute(LibraryInterface service, String isbn) {
        service.returnBook(isbn);
        // Perform post-return operations here, if needed.
        System.out.println("Book returned: " + isbn); // Example to print the returned book's isbn.
        return null;
    }
}
