package com.comiczone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;

import com.comiczone.R;
import com.comiczone.adapter.LoadMoreAdapter;
import com.comiczone.extractors.Callback;
import com.comiczone.extractors.ComicExtractor;
import com.comiczone.extractors.ComicsAndMore;
import com.comiczone.extractors.TruyenChonExtractor;
import com.comiczone.model.Comic;

import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends AppCompatActivity {

    private RecyclerView mRvComic;

    private ArrayList<Comic> lstComic;
    private LoadMoreAdapter adapterBXH;

    private Boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private LinearLayoutManager manager;
    private ArrayList<Comic> lstmore;
    private int pos = 0;
    private ArrayList<String> lstUrl;
    private Toolbar mToolbar;
    private ComicExtractor extractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initView();
        extractor = new TruyenChonExtractor(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        mToolbar.setTitle(title);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadBXH(url);

        adapterBXH = new LoadMoreAdapter(MoreActivity.this, lstComic);
        manager = new LinearLayoutManager(MoreActivity.this);
        mRvComic.setLayoutManager(manager);
        mRvComic.setHasFixedSize(true);
        mRvComic.setItemAnimator(new DefaultItemAnimator());
        mRvComic.setAdapter(adapterBXH);


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
        mToolbar = findViewById(R.id.toolbar);
        lstmore = new ArrayList<>();
        lstUrl = new ArrayList<>();
    }

    private void loadBookmore(final String url) {
        pos++;
        lstmore.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getComics(url, new Callback<List<Comic>>() {
                    @Override
                    public void accept(List<Comic> data) {
                        lstmore.addAll(data);
                        lstComic.addAll(lstmore);
                        adapterBXH.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void loadBXH(final String url) {
        lstComic.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getCommicAndMore(url, new Callback<ComicsAndMore>() {
                    @Override
                    public void accept(ComicsAndMore data) {
                        lstComic.addAll(data.getComics());
                        lstUrl.addAll(data.getMoreUrls());
                        adapterBXH.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

}
