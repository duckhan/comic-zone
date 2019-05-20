package com.comiczone.extractors;

public interface Callback<T> {
    void accept(T data);
}