package com.mastercomic.extractors;

import com.mastercomic.model.Comic;
import com.mastercomic.model.Search;

import java.util.List;

public interface ComicExtractor {
    void getComics(String url, Callback<List<Comic>> callback);
    void getCommicAndMore(String url, Callback<ComicsAndMore> callback);
    void getSearches(String keyword, Callback<List<Search>> callback);
}
