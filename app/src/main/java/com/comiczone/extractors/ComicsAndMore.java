package com.comiczone.extractors;

import com.comiczone.model.Comic;

import java.util.List;

public class ComicsAndMore {
    List<Comic> comics;
    List<String> moreUrls;

    public ComicsAndMore(List<Comic> comics, List<String> moreUrls) {
        this.comics = comics;
        this.moreUrls = moreUrls;
    }

    public List<Comic> getComics() {
        return comics;
    }

    public List<String> getMoreUrls() {
        return moreUrls;
    }
}
