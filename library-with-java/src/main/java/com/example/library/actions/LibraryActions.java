package com.example.library.actions;


import com.example.library.actions.policy.*;
import com.example.library.service.LibraryInterface;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum LibraryActions {
    REGISTER_BOOK(new RegisterBookExecutor()),
    FIND_ALL_BOOK(new FindAllBookExecutor()),
    FIND_BOOK_BY_SUBJECT(new FindBookBySubjectExecutor()),
    RENT_BOOK(new RentBookExecutor()),
    RETURN_BOOK(new ReturnBookExecutor()),
    LOST_BOOK(new LostBookExecutor()),
    DELETE_BOOK(new DeleteBookExecutor());

    private final LibraryActionExecutor<?, ?> executor;

    <T, R> LibraryActions(LibraryActionExecutor<T, R> executor) {
        this.executor = executor;
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public <T, R> R perform(LibraryInterface service, T arg) {
        if (executor != null) {
            return ((LibraryActionExecutor<T, R>) executor).execute(service, arg);
        } else {
            throw new IllegalArgumentException("Invalid executor type");
        }
    }

    public static Optional<LibraryActions> findByName(String actionName) {
        return Arrays.stream(values())
                .filter(matchesName(actionName))
                .findAny();
    }

    private static Predicate<LibraryActions> matchesName(String actionName) {
        return action -> action.name().equalsIgnoreCase(actionName);
    }
}
