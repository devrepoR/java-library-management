package com.example.library.storage;

import com.example.library.application.RentedBook;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemoryLockDataAccess implements BookDataAccess {
    private final Map<String, RentedBook> books = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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

    public boolean updateBookStatus(String isbn, RentedBook.BookStatus newStatus) {
        lock.writeLock().lock();
        try {
            RentedBook book = books.get(isbn);
            if (book != null) {
                book.updateStatus(newStatus);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
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