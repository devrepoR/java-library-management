package com.example.library.batch;

import com.example.library.application.RentedBook;
import com.example.library.storage.BookDataAccess;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BookStatusUpdater {
    private static final Logger log = Logger.getLogger(BookStatusUpdater.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final BookDataAccess dataAccess;

    public BookStatusUpdater(BookDataAccess dataAccess) {
        this.dataAccess = dataAccess;
        initializeScheduler();
    }

    private void initializeScheduler() {
        log.info("[LOG] [INIT] [BATCH]");
        scheduler.scheduleAtFixedRate(this::updateBookStatuses, 0, 10, TimeUnit.SECONDS);
    }

    private void updateBookStatuses() {
        log.info("[LOG] [BATCH] [UPDATE] [START]");
        List<RentedBook> booksUnderReview = dataAccess.findAllBooks().stream()
                .filter(book -> book.getStatus() == RentedBook.BookStatus.ORGANIZING)
                .toList();

        log.info("[LOG] [BATCH] [FIND DATA SIZE] : " + booksUnderReview.size());
        for (RentedBook book : booksUnderReview) {
            Duration organizingDuration = Duration.ofMinutes(5); // 5분 동안 정리 중 상태를 유지
            if (book.isOrganizingTimeOver(organizingDuration, LocalDateTime.now())) {
                log.info("Book Updated ORGANIZING to AVAILABLE: " + book.getIsbn());
                dataAccess.updateBookStatus(book.getIsbn(), RentedBook.BookStatus.AVAILABLE);
            }
        }
        log.info("[LOG] [BATCH] [UPDATE] [END]");
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
