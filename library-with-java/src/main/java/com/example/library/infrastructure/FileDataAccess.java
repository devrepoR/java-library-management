package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import com.example.library.utils.ConvertUtils;

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

import static com.example.library.utils.FileConstant.FILE_DELIMITER;

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
    public void addBook(RentedBook book) {
        try (BufferedWriter writer = getBufferedWriter()) {
            writer.write(bookToCsv(book));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<RentedBook> findBookByIsbn(String isbn) {
        return readBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public List<RentedBook> findAllBooks() {
        return readBooks();
    }

    @Override
    public boolean updateBookStatus(String isbn, RentedBook.BookStatus newStatus) {
        List<RentedBook> books = readBooks(); // 모든 도서 정보를 읽어옴
        boolean isUpdated = false;

        for (RentedBook book : books) {
            if (book.getIsbn().equals(isbn)) {
                book.updateStatus(newStatus); // 상태 변경
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

    private List<RentedBook> readBooks() {
        Map<String, RentedBook> bookMap = new ConcurrentHashMap<>();

        try {
            List<String> lines = Files.readAllLines(path);
            for(String line : lines) {
                String[] fields = line.split(FILE_DELIMITER);
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
        List<RentedBook> books = readBooks();
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

    private void writeBooks(List<RentedBook> books) {
        try (BufferedWriter writer = getBufferedWriter()) {
            deleteAll();
            for (RentedBook book : books) {
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
        return Files.newBufferedWriter(path, StandardOpenOption.CREATE);
    }

    private RentedBook csvToBook(String csv) {
        String[] fields = csv.split(FILE_DELIMITER);
        Integer totalCnt = ConvertUtils.convert(fields[3], Integer::parseInt);
        return new RentedBook(
                new Book(fields[0], fields[1], fields[2], totalCnt)
                , ConvertUtils.parseLocalDateTime(fields[4])
                , ConvertUtils.parseLocalDateTime(fields[5])
                , RentedBook.BookStatus.valueOf(fields[6])
        );
    }

    private String bookToCsv(RentedBook rentedBook) {
        return rentedBook.toString();
    }
}
