package com.mastercomic.extractors;

public interface Callback<T> {
    void accept(T data);
}