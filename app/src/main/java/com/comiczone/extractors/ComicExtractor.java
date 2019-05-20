package com.comiczone.extractors;

import com.comiczone.model.Comic;
import com.comiczone.model.Search;

import java.util.List;

public interface ComicExtractor {
    void getComics(String url, Callback<List<Comic>> callback);
    void getCommicAndMore(String url, Callback<ComicsAndMore> callback);
    void getSearches(String keyword, Callback<List<Search>> callback);
}
