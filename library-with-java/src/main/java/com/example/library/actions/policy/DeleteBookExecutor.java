package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.LibraryInterface;

public class DeleteBookExecutor implements LibraryActionExecutor<String, Boolean> {

    @Override
    public Boolean execute(LibraryInterface service, String isbn) {
        return service.deleteBook(isbn);
    }
}
