package com.comiczone.activity;

import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.comiczone.R;
import com.comiczone.adapter.SearchAdapter;
import com.comiczone.adapter.ViewPagerAdapter;
import com.comiczone.extractors.Callback;
import com.comiczone.extractors.ComicExtractor;
import com.comiczone.extractors.TruyenChonExtractor;
import com.comiczone.model.Search;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"StatementWithEmptyBody", "EmptyMethod"})
public class TopActivity extends AppCompatActivity implements TextWatcher {
    private TabLayout tabLayout;

    private AutoCompleteTextView mEditAuto;
    private SearchAdapter adapterSearch;
    private ArrayList<Search> lstSearch;
    private ComicExtractor extractor;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractor = new TruyenChonExtractor(this);
        setContentView(R.layout.activity_top);
        initView();
        mEditAuto.addTextChangedListener(this);
        adapterSearch = new SearchAdapter(this, R.layout.item_custom_search, lstSearch);
        mEditAuto.setAdapter(adapterSearch);
        mEditAuto.setThreshold(0);
        addControl();
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.white));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    private void initView() {
        mEditAuto = findViewById(R.id.editAuto);
        lstSearch = new ArrayList<>();
    }

    private void addControl() {
        ViewPager pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        FragmentManager manager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String la = mEditAuto.getText().toString();
                String keyword = la.replaceAll(" ", "+");
                extractor.getSearches(keyword, new Callback<List<Search>>() {
                    @Override
                    public void accept(List<Search> data) {
                        lstSearch.addAll(data);
                        adapterSearch = new SearchAdapter(TopActivity.this, R.layout.item_custom_search, lstSearch);
                        mEditAuto.setAdapter(adapterSearch);
                        adapterSearch.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


}
