package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.service.LibraryInterface;
import com.example.library.application.RentedBook;

public class RentBookExecutor implements LibraryActionExecutor<String, RentedBook> {
    @Override
    public RentedBook execute(LibraryInterface service, String isbn) {
        return service.rentBook(isbn);
    }
}
