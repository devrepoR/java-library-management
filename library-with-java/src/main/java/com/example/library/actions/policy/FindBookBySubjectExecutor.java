package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.RentedBook;
import com.example.library.service.LibraryInterface;

public class FindBookBySubjectExecutor implements LibraryActionExecutor<String, RentedBook> {

    @Override
    public RentedBook execute(LibraryInterface service, String arg) {
        return service.findBookWithSubject(arg);
    }
}