package com.example.library.actions.policy;

import com.example.library.actions.LibraryActionExecutor;
import com.example.library.application.RentedBook;
import com.example.library.service.LibraryInterface;

public class RegisterBookExecutor implements LibraryActionExecutor<RentedBook, RentedBook> {
    @Override
    public RentedBook execute(LibraryInterface service, RentedBook rentedBook) {
        return service.regist(rentedBook);
    }
}