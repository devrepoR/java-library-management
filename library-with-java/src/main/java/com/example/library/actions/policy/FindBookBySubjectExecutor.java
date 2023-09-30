package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.RentedBook;
import com.example.library.service.LibraryInterface;

import java.util.Optional;

public class FindBookBySubjectExecutor implements LibraryActionExecutor<String, Optional<RentedBook>> {

    @Override
    public Optional<RentedBook> execute(LibraryInterface service, String arg) {
        return service.findBookWithSubject(arg);
    }
}