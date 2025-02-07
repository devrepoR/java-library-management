package com.example.library.infrastructure;

import com.example.library.domain.Book;
import com.example.library.domain.RentedBook;
import com.example.library.utils.ConvertUtils;

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

import static com.example.library.utils.FileConstant.FILE_DELIMITER;

public class FileChannelDataAccess implements BookDataAccess {
    private static final Logger log = Logger.getLogger(FileChannelDataAccess.class.getName());
    private final ReadWriteLock readWriteLock;
    private final Path path;

    public FileChannelDataAccess(String filePath) {
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("파일 경로가 잘못되었습니다.");
        }
        this.path = Paths.get(filePath);
        validatePath();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void addBook(RentedBook book) {
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
    public boolean changeBook(RentedBook book) {
        readWriteLock.writeLock().lock();
        try {
            List<RentedBook> books = readBooks();
            boolean isUpdated = false;
            for (int i = 0; i < books.size(); i++) {
                RentedBook rentedBook = books.get(i);
                if (rentedBook.getIsbn().equals(book.getIsbn())) {
                    books.set(i, book);
                    isUpdated = true;
                    break;
                }
            }
            if (isUpdated) {
                // 전체 파일 다시 쓰기
                writeBooks(books);
            }
            return isUpdated;
        } catch(Exception e) {
            throw new RuntimeException("도서 정보 변경 실패", e);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeBookByIsbn(String isbn) {
        List<RentedBook> books = readBooks();
        int initialSize = books.size();
        books.removeIf(book -> book.getIsbn().equals(isbn));
        if (books.size() < initialSize) {
            // 전체 파일 다시 쓰기
            writeBooks(books);
            return true;
        }
        return false;
    }

    public int countBooks() {
        return readBooks().size();
    }

    public void deleteAll() {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {} catch (IOException e) {
            throw new RuntimeException("데이터 삭제 실패", e);
        }
    }

    private void validatePath() {
        // Ensure the parent directory exists
        Path parentDir = path.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            try {
                Path directories = Files.createDirectories(parentDir);
                log.info("디렉토리 생성 : " + directories);
            } catch (IOException e) {
                log.warning("디렉토리 생성 실패 : " + parentDir);
                throw new RuntimeException("디렉토리 생성 실패 : " + parentDir, e);
            }
        }

        // Ensure the file exists
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                log.warning("파일 생성 실패 : " + path);
                throw new RuntimeException("파일 생성 실패 : " + path, e);
            }
        }

        if (!Files.isWritable(path)) {
            log.warning(path + " 에 쓰기 권한이 없습니다.");
            throw new RuntimeException(path + " 에 쓰기 권한이 없습니다.");
        }
    }

    private void writeBooks(List<RentedBook> books) {
        readWriteLock.writeLock().lock();
        try {
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                channel.truncate(0);
                for (RentedBook book : books) {
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

    private List<RentedBook> readBooks() {
        readWriteLock.readLock().lock();
        try {
            List<RentedBook> books = new ArrayList<>();
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
            log.info("[LOG] [READ] [SIZE : " + books.size() + "]");
            return books;
        } finally {
            readWriteLock.readLock().unlock();
        }
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
