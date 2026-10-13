package com.skillnexus.library;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final List<BookObserver> observers = new ArrayList<>();
    public void subscribe(BookObserver observer) { if (observer != null && !observers.contains(observer)) observers.add(observer); }
    public void unsubscribe(BookObserver observer) { observers.remove(observer); }
    public void notifyObservers(String message) { for (BookObserver observer : List.copyOf(observers)) observer.onBookEvent(message); }
}