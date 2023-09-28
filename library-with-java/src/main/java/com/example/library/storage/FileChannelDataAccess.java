package com.example.library.storage;

import com.example.library.application.Book;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class FileChannelDataAccess implements BookDataAccess {
    private static final Logger log = Logger.getLogger(FileChannelDataAccess.class.getName());
    private final Path path;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public FileChannelDataAccess(String filePath) throws IOException {
        this.path = Paths.get(filePath);

        // Ensure the parent directory exists
        Path parentDir = path.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                throw new IOException("디렉토리 생성 실패 : " + parentDir, e);
            }
        }

        // Ensure the file exists
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new IOException("파일 생성 실패 : " + path, e);
            }
        }

        if (!Files.isWritable(path)) {
            throw new IOException(path + " 에 쓰기 권한이 없습니다.");
        }
    }

    @Override
    public void addBook(Book book) {
        readWriteLock.writeLock().lock();
        try {
            // 임시 파일 생성
            Path tempFile = Files.createTempFile("book-", ".tmp");
            try (FileChannel tempChannel = FileChannel.open(tempFile, StandardOpenOption.WRITE)) {
                String data = bookToCsv(book) + System.lineSeparator();
                tempChannel.write(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
            }
            // 원본 파일에 임시 파일 내용 추가
            try (FileChannel mainChannel = FileChannel.open(path, StandardOpenOption.APPEND)) {
                try (FileChannel srcChannel = FileChannel.open(tempFile)) {
                    mainChannel.transferFrom(srcChannel, mainChannel.size(), srcChannel.size());
                }
            }
            Files.delete(tempFile); // 임시 파일 삭제
        } catch (IOException e) {
            throw new RuntimeException("파일에 도서 정보를 등록할 수 없습니다.", e);
        } finally {
            readWriteLock.writeLock().unlock();
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
        readWriteLock.writeLock().lock();
        try {
            List<Book> books = readBooks();
            boolean isUpdated = false;
            for (Book book : books) {
                if (book.getIsbn().equals(isbn)) {
                    book.changeStatus(newStatus);
                    isUpdated = true;
                    break;
                }
            }
            if (isUpdated) {
                // 전체 파일 다시 쓰기
                writeBooks(books);
            }
            return isUpdated;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeBookByIsbn(String isbn) {
        List<Book> books = readBooks();
        int initialSize = books.size();
        books.removeIf(book -> book.getIsbn().equals(isbn));
        if (books.size() < initialSize) {
            // 전체 파일 다시 쓰기
            writeBooks(books);
            return true;
        }
        return false;
    }

    private void writeBooks(List<Book> books) {
        readWriteLock.writeLock().lock();
        try {
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                channel.truncate(0);
                for (Book book : books) {
                    String data = bookToCsv(book) + System.lineSeparator();
                    channel.write(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
                }
            } catch (IOException e) {
                throw new RuntimeException("Error writing books to file", e);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public int countBooks() {
        return readBooks().size();
    }

    private List<Book> readBooks() {
        readWriteLock.readLock().lock();
        try {
            List<Book> books = new ArrayList<>();
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                channel.read(buffer);
                buffer.flip();
                String content = new String(buffer.array(), StandardCharsets.UTF_8);

                for (String line : content.split(System.lineSeparator())) {
                    if (!line.isBlank()) {
                        books.add(csvToBook(line));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("읽기 실패", e);
            }
            return books;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void deleteAll() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            throw new IOException("파일 내용을 삭제 할 수 없습니다. : " + path, e);
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
