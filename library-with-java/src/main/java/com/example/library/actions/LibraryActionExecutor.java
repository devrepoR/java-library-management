package com.example.library.actions;

import com.example.library.application.LibraryInterface;

public interface LibraryActionExecutor<T, R> {
    R execute(LibraryInterface service, T arg);
}