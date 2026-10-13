package com.skillnexus.library;

import java.util.List;

public interface LibraryRepository {
    List<Book> loadBooks();
    void saveBooks(List<Book> books);
}
