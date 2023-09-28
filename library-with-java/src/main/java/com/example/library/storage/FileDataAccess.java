package com.example.library.storage;

import com.example.library.application.Book;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class FileDataAccess implements BookDataAccess {
    private static final Logger log = Logger.getLogger(FileDataAccess.class.getName());
    private final Path path;

    public FileDataAccess(String filePath) throws IOException {
        this.path = Paths.get(filePath);

        // Ensure the parent directory exists
        Path parentDir = path.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new IOException("Failed to create directory at " + parentDir, e);
            }
        }

        // Ensure the file exists
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new IOException("Failed to create file at " + path, e);
            }
        }

        if (!Files.isWritable(path)) {
            throw new IOException("File at " + path + " is not writable");
        }
    }

    @Override
    public void addBook(Book book) {
        try (BufferedWriter writer = getBufferedWriter()) {
            writer.write(bookToCsv(book));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return readBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public List<Book> findAllBooks() {
        return readBooks();
    }

    @Override
    public boolean updateBookStatus(String isbn, Book.BookStatus newStatus) {
        List<Book> books = readBooks(); // 모든 도서 정보를 읽어옴
        boolean isUpdated = false;

        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                book.changeStatus(newStatus); // 상태 변경
                isUpdated = true;
                log.info("[LOG] [UPDATED] => [" + isUpdated + "] [" + book + "]");
                break; // ISBN이 고유하다고 가정하고, 찾으면 loop 종료
            }
        }
        if (isUpdated) {
            writeBooks(books); // 변경된 도서 정보로 전체 파일 다시 쓰기
        }

        return isUpdated;
    }

    private List<Book> readBooks() {
        Map<String, Book> bookMap = new ConcurrentHashMap<>();

        try {
            List<String> lines = Files.readAllLines(path);
            for(String line : lines) {
                String[] fields = line.split(",");
                String isbn = fields[0];

                bookMap.put(isbn, csvToBook(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(bookMap.values());
    }

    @Override
    public boolean removeBookByIsbn(String isbn) {
        List<Book> books = readBooks();
        int initialSize = books.size();

        books.removeIf(book -> {
            log.info("[LOG] [REMOVE] => [" + book.getIsbn() + "]");
            return book.getIsbn().equals(isbn);
        });
        if (books.size() < initialSize) {
            writeBooks(books);
            return true;
        }
        return false;
    }

    public int countBooks() {
        return readBooks().size();
    }

    private final Object writeLock = new Object();

    private void writeBooks(List<Book> books) {
        try (BufferedWriter writer = getBufferedWriter()) {
            deleteAll();
            for (Book book : books) {
                writer.write(bookToCsv(book));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            throw new IOException("Failed to clear the file content at " + path, e);
        }
    }

    // Creating a BufferedWriter instance in a separate method to avoid duplication
    private BufferedWriter getBufferedWriter() throws IOException {
        return Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private Book csvToBook(String csv) {
        String[] fields = csv.split(",");
        return new Book(fields[0], fields[1], fields[2], fields[3], Book.BookStatus.of(fields[4]));
    }

    private String bookToCsv(Book book) {
        return String.join(",", book.getIsbn(), book.getSubject(), book.getAuthor(), String.valueOf(book.getTotalPageCnt()), book.getStatus().name());
    }
}
