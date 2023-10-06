package com.example.library.infrastructure;

import com.example.library.domain.RentedBook;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemoryLockDataAccess implements BookDataAccess {
    private final Map<String, RentedBook> books;
    private final ReadWriteLock lock;

    public MemoryLockDataAccess() {
        books = new HashMap<>();
        lock = new ReentrantReadWriteLock();
    }

    public void addBook(RentedBook book) {
        lock.writeLock().lock();
        try {
            books.put(book.getIsbn(), book);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<RentedBook> findBookByIsbn(String isbn) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(books.get(isbn));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<RentedBook> findAllBooks() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(books.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int countBooks() {
        return books.size();
    }

    @Override
    public boolean changeBook(RentedBook book) {
        lock.writeLock().lock();
        try {
            if (books.containsKey(book.getIsbn())) {
                books.put(book.getIsbn(), book);
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    public boolean removeBookByIsbn(String isbn) {
        lock.writeLock().lock();
        try {
            return books.remove(isbn) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}