package com.example.library.actions;

import com.example.library.service.LibraryInterface;

public interface LibraryActionExecutor<T, R> {
    R execute(LibraryInterface service, T arg);
}