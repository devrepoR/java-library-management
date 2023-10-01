package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.LibraryInterface;
import com.example.library.domain.RentedBook;

public class RentBookExecutor implements LibraryActionExecutor<String, RentedBook> {
    @Override
    public RentedBook execute(LibraryInterface service, String isbn) {
        return service.rentBook(isbn);
    }
}
