package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.domain.RentedBook;
import com.example.library.application.LibraryInterface;

import java.util.List;

public class FindAllBookExecutor implements LibraryActionExecutor<String, List<RentedBook>> {
    @Override
    public List<RentedBook> execute(LibraryInterface service, String arg) {
        return service.books();
    }
}