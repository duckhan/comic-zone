package com.mastercomic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.mastercomic.R;
import com.mastercomic.adapter.ComicAdapter;
import com.mastercomic.adapter.SearchAdapter;
import com.mastercomic.extractors.Callback;
import com.mastercomic.model.Comic;
import com.mastercomic.model.Search;
import com.mastercomic.until.CheckConnection;
import com.mastercomic.extractors.TruyenChonExtractor;
import com.mastercomic.until.Link;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

@SuppressWarnings({"EmptyMethod"})
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private ArrayList<Comic> lstNewUpdate;
    private ArrayList<Comic> lstHotTrend;
    private BannerSlider mSliderBanner;

    private ComicAdapter newUpdateAdapter;

    private ShimmerRecyclerView rv_NewUpdate;
    private ShimmerRecyclerView rv_HotTrend;
    private AutoCompleteTextView mEditAuto;
    private ArrayList<String> urlBanner;
    private SearchAdapter adapterSearch;
    private ArrayList<Search> lstSearch;
    private List<Banner> banners;
    private RelativeLayout mRoot;
    private CheckConnection checkConnection;
    private TruyenChonExtractor extractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.extractor = new TruyenChonExtractor(this);
        setContentView(R.layout.activity_main);
        initView();
        checkConnection = new CheckConnection(MainActivity.this, mRoot);
        checkConnection.checkConnection();
        rv_NewUpdate.showShimmerAdapter();
        rv_HotTrend.showShimmerAdapter();
        loadComicNewUpdate();
        loadComicHot();
        Realm.init(this);
        mEditAuto.addTextChangedListener(this);
        adapterSearch = new SearchAdapter(this, R.layout.item_custom_search, lstSearch);
        mEditAuto.setAdapter(adapterSearch);
        mEditAuto.setThreshold(0);

        mSliderBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, PageComicActivity.class);
                intent.putExtra("url", urlBanner.get(position));
                startActivity(intent);
            }
        });
    }


    private void initView() {
        lstNewUpdate = new ArrayList<>();
        lstHotTrend = new ArrayList<>();
        banners = new ArrayList<>();
        urlBanner = new ArrayList<>();
        lstSearch = new ArrayList<>();
        mSliderBanner = findViewById(R.id.banner_slider);
        rv_NewUpdate = findViewById(R.id.rv_NewUpdate);
        rv_HotTrend = findViewById(R.id.rv_HotTrend);
        Button mTheLoaiBtn = findViewById(R.id.btn_TheLoai);
        mTheLoaiBtn.setOnClickListener(this);
        Button mBXHBtn = findViewById(R.id.btn_BXH);
        mBXHBtn.setOnClickListener(this);
        Button mFavoriteBtn = findViewById(R.id.btn_Favorite);
        mFavoriteBtn.setOnClickListener(this);
        mEditAuto = findViewById(R.id.editAuto);
        mRoot = findViewById(R.id.root);
        Button mTruyenmoiBtn = findViewById(R.id.btn_newCommic);
        mTruyenmoiBtn.setOnClickListener(this);
        Button mTruyenhotBtn = findViewById(R.id.btn_hotCommic);
        mTruyenhotBtn.setOnClickListener(this);
    }

    private void loadComicNewUpdate() {
        lstNewUpdate.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getComics(Link.URL_HOMEPAGE, new Callback<List<Comic>>() {
                    @Override
                    public void accept(List<Comic> data) {
                        lstNewUpdate.addAll(data);
                        for (int i = 0; i < 10; i++) {
                            String url = lstNewUpdate.get(i).getLinkComic();
                            String thumb = lstNewUpdate.get(i).getThumb();
                            banners.add(new RemoteBanner(thumb));
                            urlBanner.add(url);
                        }
                        mSliderBanner.setBanners(banners);
                        mSliderBanner.setInterval(10000);
                        mSliderBanner.setDefaultIndicator(2);
                        mSliderBanner.setMustAnimateIndicators(true);
                        mSliderBanner.setLoopSlides(true);
                        rv_NewUpdate.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstNewUpdate);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_NewUpdate.setLayoutManager(horizontalLayout);
                                rv_NewUpdate.setHasFixedSize(true);
                                rv_NewUpdate.setItemAnimator(new DefaultItemAnimator());
                                rv_NewUpdate.setAdapter(newUpdateAdapter);
                                rv_NewUpdate.hideShimmerAdapter();
                            }
                        });
                    }
                });
            }
        }).start();

    }

    private void loadComicHot() {
        lstHotTrend.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getComics(Link.URL_HOT_TREND, new Callback<List<Comic>>() {
                    @Override
                    public void accept(List<Comic> data) {
                        lstHotTrend.addAll(data);
                        rv_HotTrend.post(new Runnable() {
                            @Override
                            public void run() {
                                newUpdateAdapter = new ComicAdapter(MainActivity.this, lstHotTrend);
                                LinearLayoutManager horizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                rv_HotTrend.setLayoutManager(horizontalLayout);
                                rv_HotTrend.setHasFixedSize(true);
                                rv_HotTrend.setItemAnimator(new DefaultItemAnimator());
                                rv_HotTrend.setAdapter(newUpdateAdapter);
                                rv_HotTrend.hideShimmerAdapter();
                            }
                        });
                    }
                });

            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        Intent more = new Intent(MainActivity.this, MoreActivity.class);

        switch (v.getId()) {
            case R.id.btn_TheLoai:
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_BXH:
                Intent inten = new Intent(MainActivity.this, TopActivity.class);
                startActivity(inten);
                break;
            case R.id.btn_Favorite:
                Intent inte = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(inte);
                break;
            case R.id.btn_newCommic:
                more.putExtra("url", Link.URL_HOMEPAGE);
                more.putExtra("title", "Truyện mới cập nhật");
                startActivity(more);
                break;
            case R.id.btn_hotCommic:
                more.putExtra("url", Link.URL_HOT_TREND);
                more.putExtra("title", "Truyện hot");
                startActivity(more);
                break;
            default:
                break;
        }
    }

    public void onTextChanged(CharSequence arg0, int arg1,
                              int arg2, int arg3) {
    }

    public void afterTextChanged(Editable arg0) {
        try {
            requestSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    private void requestSearch() {
        lstSearch.clear();
        final String la = mEditAuto.getText().toString();
        String keyword = la.replaceAll(" ", "+");
        extractor.getSearches(keyword, new Callback<List<Search>>() {
            @Override
            public void accept(List<Search> data) {
                lstSearch.addAll(data);
                adapterSearch = new SearchAdapter(MainActivity.this, R.layout.item_custom_search, lstSearch);
                mEditAuto.setAdapter(adapterSearch);
                adapterSearch.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("status:", "onresume");
        checkConnection.checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("status:", "onPause");
    }
}
