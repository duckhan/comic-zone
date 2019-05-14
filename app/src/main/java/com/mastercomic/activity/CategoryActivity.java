package com.mastercomic.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.mastercomic.R;
import com.mastercomic.adapter.LoadMoreAdapter;
import com.mastercomic.extractors.Callback;
import com.mastercomic.extractors.ComicExtractor;
import com.mastercomic.extractors.ComicsAndMore;
import com.mastercomic.extractors.TruyenChonExtractor;
import com.mastercomic.model.Chapter;
import com.mastercomic.model.Comic;
import com.mastercomic.until.Link;
import com.mastercomic.until.ParserUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView mRvComic;

    private ArrayList<Chapter> lstTheLoai;
    private ArrayList<Comic> lstComic;
    private ArrayList<String> lstName;

    // private ChapterAdapter KindAdapter;
    private LoadMoreAdapter comicAdapter;

    private PickerUI mUiViewPicker;


    private Boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private LinearLayoutManager manager;
    private ArrayList<Comic> listMore;
    private int pos = 0;
    private ArrayList<String> lstUrl;
    private Toolbar mToolbar;
    private ComicExtractor extractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initView();
        extractor = new TruyenChonExtractor(this);
        mToolbar.setTitle(getString(R.string.categoryComic));
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listMore = new ArrayList<>();
        loadCategory();


        comicAdapter = new LoadMoreAdapter(CategoryActivity.this, lstComic);
        manager = new LinearLayoutManager(CategoryActivity.this);
        mRvComic.setLayoutManager(manager);
        mRvComic.setHasFixedSize(true);
        mRvComic.setItemAnimator(new DefaultItemAnimator());
        mRvComic.setAdapter(comicAdapter);

        lstUrl = new ArrayList<>();

        mUiViewPicker.setOnClickItemPickerUIListener(new PickerUI.PickerUIItemClickListener() {
            @Override
            public void onItemClickPickerUI(int which, int position, String valueResult) {
                loadBook(lstTheLoai.get(position).getUrl());
            }
        });

        setScrollRV();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setScrollRV() {
        mRvComic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    if (pos <= lstUrl.size()) {
                        loadBookmore(lstUrl.get(pos));
                    }
                }
            }
        });
    }

    private void initView() {
        mRvComic = findViewById(R.id.rvComic);
        lstComic = new ArrayList<>();
        lstTheLoai = new ArrayList<>();
        lstName = new ArrayList<>();
        mUiViewPicker = findViewById(R.id.picker_ui_view);

        mToolbar = findViewById(R.id.toolbar);
    }

    private void loadBook(final String url) {
        lstComic.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getCommicAndMore(url, new Callback<ComicsAndMore>() {
                    @Override
                    public void accept(ComicsAndMore data) {
                        lstComic.addAll(data.getComics());
                        lstUrl.addAll(data.getMoreUrls());
                        comicAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void loadBookmore(final String url) {
        pos++;
        listMore.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getComics(url, new Callback<List<Comic>>() {
                    @Override
                    public void accept(List<Comic> data) {
                        listMore.addAll(data);
                        lstComic.addAll(listMore);
                        comicAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void loadCategory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(CategoryActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_KIND, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lstTheLoai.clear();
                        Document document = Jsoup.parse(response);
                        Elements start = document.select("div.box.darkBox.genres.Module.Module-204");
                        Elements all = start.select("ul.nav");
                        Elements allli = all.select("li");
                        Elements category = allli.select("a");
                        for (Element element : category) {
                            String url = element.attr("href");
                            String name = element.text();
                            lstTheLoai.add(new Chapter(name, url));
                            lstName.add(name);
                        }

                        lstTheLoai.remove(lstTheLoai.size() - 1);
                        lstTheLoai.remove(lstTheLoai.size() - 1);
                        lstName.remove(lstName.size() - 1);
                        lstName.remove(lstName.size() - 1);
                        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                                .withItems(lstName)
                                .withAutoDismiss(false)
                                .withColorTextCenter(Color.WHITE)
                                .withColorTextNoCenter(Color.DKGRAY)
                                .withItemsClickables(false)
                                .withUseBlur(false)
                                .build();
                        mUiViewPicker.setSettings(pickerUISettings);
                        loadBook(lstTheLoai.get(5).getUrl());
                        mUiViewPicker.slide(5);

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
        }).start();
    }


}
