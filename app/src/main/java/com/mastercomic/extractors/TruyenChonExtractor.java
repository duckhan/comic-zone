package com.mastercomic.extractors;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mastercomic.activity.MoreActivity;
import com.mastercomic.model.Comic;
import com.mastercomic.model.Search;
import com.mastercomic.until.Link;
import com.mastercomic.until.ParserUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TruyenChonExtractor implements ComicExtractor {

    private Context context;

    public TruyenChonExtractor(Context context) {
        this.context = context;
    }

    @Override
    public void getComics(String url, final Callback<List<Comic>> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        final List<Comic> results = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document document = Jsoup.parse(response);
                Elements all = document.select("div#ctl00_divCenter");
                Elements sub = all.select(".item");
                for (Element element : sub) {
                    Comic comic = getComic(element);
                    results.add(comic);
                }
                if (callback != null) {
                    callback.accept(results);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                150000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void getCommicAndMore(String url, final Callback<ComicsAndMore> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> lstUrl = new ArrayList<>();
                List<Comic> lstComic = new ArrayList<>();
                final Document document = Jsoup.parse(response);
                Elements more = document.select("div#ctl00_mainContent_ctl00_divPager");
                Log.e("name:", more.toString());
                Elements more1 = more.select("ul.pagination");
                Elements allli = more1.select("li");
                Elements next = allli.select("a");
                for (Element element : next) {
                    String url = element.attr("href");
                    Log.e("name:", url);
                    lstUrl.add(url);
                }

                Elements all = document.select("div#ctl00_divCenter");
                Elements sub = all.select(".item");
                for (Element element : sub) {
                    lstComic.add(getComic(element));
                }
                if (callback != null) {
                    callback.accept(new ComicsAndMore(lstComic, lstUrl));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                150000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void getSearches(String keyword, final Callback<List<Search>> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        String urlsearch = Link.URL_SEARCH + keyword;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlsearch, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Search> result = new ArrayList<>();
                Document document = Jsoup.parse(response);
                Elements all = document.select("div#ctl00_divCenter");
                Elements sub = all.select(".item");
                for (Element element : sub) {
                    result.add(getSearch(element));
                }
                if (callback != null) {
                    callback.accept(result);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Log.d("EXTRACTOR", res);
                    } catch (UnsupportedEncodingException e) {
                        Log.e("EXTRACTOR", "Search error", e);
                    }
                }
            }
        });
        requestQueue.add(stringRequest);
    }


    private Comic getComic(Element element) {
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


    private Search getSearch(Element element) {
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
