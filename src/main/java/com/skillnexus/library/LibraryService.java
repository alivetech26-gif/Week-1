package com.skillnexus.library;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    private final LibraryRepository database;
    private final NotificationService notifications;
    private final List<Book> books;

    public LibraryService() { this(LibraryDatabase.getInstance(), new NotificationService()); }
    public LibraryService(LibraryRepository database, NotificationService notifications) {
        this.database = database;
        this.notifications = notifications;
        this.books = new ArrayList<>(database.loadBooks());
    }
    public NotificationService notifications() { return notifications; }
    public List<Book> getAllBooks() { return List.copyOf(books); }
    public Book findBook(int id) { return books.stream().filter(b -> b.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("Book not found: " + id)); }
    public Book addBook(String title, String author) {
        int id = books.stream().mapToInt(Book::getId).max().orElse(0) + 1;
        Book book = BookFactory.createBook(id, title, author);
        books.add(book); persist(); notifications.notifyObservers("Book added: " + book.getTitle()); return book;
    }
    public void updateBook(int id, String title, String author) {
        Book book = findBook(id); book.setTitle(title); book.setAuthor(author); persist(); notifications.notifyObservers("Book updated: " + book.getTitle());
    }
    public void deleteBook(int id) {
        Book book = findBook(id); books.remove(book); persist(); notifications.notifyObservers("Book deleted: " + book.getTitle());
    }
    public void setAvailability(int id, boolean available) {
        Book book = findBook(id); book.setAvailable(available); persist();
        notifications.notifyObservers((available ? "Book returned: " : "Book borrowed: ") + book.getTitle());
    }
    private void persist() { database.saveBooks(books); }
}