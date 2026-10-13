package com.skillnexus.library;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean available = true;

    public Book(int id, String title, String author) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty.");
        this.id = id;
        this.title = title.trim();
        this.author = author.trim();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public void setTitle(String title) { if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty."); this.title = title.trim(); }
    public void setAuthor(String author) { if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty."); this.author = author.trim(); }
    public void setAvailable(boolean available) { this.available = available; }
    @Override public String toString() { return id + " | " + title + " | " + author + " | " + (available ? "Available" : "Borrowed"); }
}