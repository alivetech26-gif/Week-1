package com.skillnexus.library;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LibraryDatabase implements LibraryRepository {
    private static final LibraryDatabase INSTANCE = new LibraryDatabase();
    private static final Path FILE = Path.of("books.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type BOOK_LIST = new TypeToken<List<Book>>() {}.getType();
    private LibraryDatabase() {}
    public static LibraryDatabase getInstance() { return INSTANCE; }
    public synchronized List<Book> loadBooks() {
        if (!Files.exists(FILE)) return new ArrayList<>();
        try { List<Book> books = GSON.fromJson(Files.readString(FILE), BOOK_LIST); return books == null ? new ArrayList<>() : new ArrayList<>(books); }
        catch (IOException | RuntimeException e) { throw new IllegalStateException("Unable to load books.json", e); }
    }
    public synchronized void saveBooks(List<Book> books) {
        try { Files.writeString(FILE, GSON.toJson(books)); }
        catch (IOException e) { throw new IllegalStateException("Unable to save books.json", e); }
    }
}