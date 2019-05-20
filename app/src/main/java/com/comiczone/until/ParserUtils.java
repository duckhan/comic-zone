package com.comiczone.until;

import com.comiczone.model.Comic;
import com.comiczone.model.Search;

import org.jsoup.nodes.Element;

public class ParserUtils {
    public Comic getComic(Element element) {
        Element hinhanh = element.getElementsByTag("img").get(0);
        Element linktruyen = element.getElementsByTag("a").get(0);
        Element sochuong = element.getElementsByTag("a").get(2);
        Element tentruyen = element.getElementsByTag("h3").get(0);
        Element luotxem = element.getElementsByTag("span").get(0);
        Element luotxem2 = null;

        try {
            if (element.getElementsByTag("span").size() > 1) {
                luotxem2 = element.getElementsByTag("span").get(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String thumb;
        String thumb1 = hinhanh.attr("src");
        String thumb2 = hinhanh.attr("data-original");
        if (thumb2.equals("")) {
            thumb = thumb1;
        } else {
            thumb = thumb2;
        }
        String name = tentruyen.text();
        String link = linktruyen.attr("href");
        String view;
        if (luotxem.text().equals("")) {
            view = luotxem2.text();
        } else {
            view = luotxem.text();
        }
        String string = view;
        String[] parts = string.split(" ");
        String viewCount = parts[0];
        if (thumb.startsWith("http:") || thumb.startsWith("https:")) {
        } else {
            thumb = "http:" + thumb;
        }
        String chapter = sochuong.text();
        return new Comic(name, viewCount, thumb, chapter, link);
    }


    public Search getSearch(Element element) {
        Element hinhanh = element.getElementsByTag("img").get(0);
        Element linktruyen = element.getElementsByTag("a").get(0);
        Element tentruyen = element.getElementsByTag("h3").get(0);
        String thumb;
        String thumb1 = hinhanh.attr("src");
        String thumb2 = hinhanh.attr("data-original");
        if (thumb2.equals("")) {
            thumb = thumb1;
        } else {
            thumb = thumb2;
        }
        String name = tentruyen.text();

        String link = linktruyen.attr("href");
        if (thumb.startsWith("http:") || thumb.startsWith("https:")) {
        } else {
            thumb = "http:" + thumb;
        }
        return new Search(name, thumb, link);
    }
}
