package com.comiczone.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.comiczone.R;
import com.comiczone.adapter.LoadMoreAdapter;
import com.comiczone.extractors.Callback;
import com.comiczone.extractors.ComicExtractor;
import com.comiczone.extractors.ComicsAndMore;
import com.comiczone.extractors.TruyenChonExtractor;
import com.comiczone.model.Comic;
import com.comiczone.until.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings({"ConstantConditions", "StatementWithEmptyBody"})
public class TopMonthFragment extends Fragment {

    private LinearLayout mNoPost;
    private RecyclerView mNgayRv;

    private Boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private LinearLayoutManager manager;
    private int pos = 0;
    private ArrayList<String> lstUrl;
    private ArrayList<Comic> lstmore;
    private ArrayList<Comic> lstBXH;
    private LoadMoreAdapter adapterBXH;
    private SwipeRefreshLayout mSwipeMain;
    private ComicExtractor extractor;

    private void initView(@NonNull final View itemView) {
        lstBXH = new ArrayList<>();
        lstUrl = new ArrayList<>();
        lstmore = new ArrayList<>();
        mNoPost = itemView.findViewById(R.id.noPost);
        mNgayRv = itemView.findViewById(R.id.rv_Ngay);
        adapterBXH = new LoadMoreAdapter(getContext(), lstBXH);
        manager = new LinearLayoutManager(getContext());
        mSwipeMain = itemView.findViewById(R.id.swipeRefreshLayout);
    }

    public TopMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_month, container, false);

        initView(view);
        extractor = new TruyenChonExtractor(getContext());
        loadBXH();
        mNgayRv.setLayoutManager(manager);
        mNgayRv.setHasFixedSize(true);
        mNgayRv.setItemAnimator(new DefaultItemAnimator());
        mNgayRv.setAdapter(adapterBXH);
        mSwipeMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mNgayRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (pos <= lstUrl.size() - 1) {
                        loadBookmore(lstUrl.get(pos));
                    }
                }
            }
        });
        return view;
    }

    private void refreshItems() {
        loadBXH();

    }

    private void onItemsLoadComplete() {
        mSwipeMain.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lstBXH.size() > 0) {
            mNoPost.setVisibility(View.GONE);
        } else {
            mNoPost.setVisibility(View.VISIBLE);
        }

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
                        lstBXH.addAll(lstmore);
                        adapterBXH.notifyDataSetChanged();
                    }
                });
//                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Document document = Jsoup.parse(response);
//                        Elements all = document.select("div#ctl00_divCenter");
//                        Elements sub = all.select(".item");
//                        for (Element element : sub) {
//                            lstmore.add(ParserUtils.getComic(element));
//                        }
//                        lstBXH.addAll(lstmore);
//                        adapterBXH.notifyDataSetChanged();
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                });
//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        150000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                requestQueue.add(stringRequest);
            }
        }).start();
    }

    private void loadBXH() {
        lstBXH.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                extractor.getCommicAndMore(Link.URL_MONTH, new Callback<ComicsAndMore>() {
                    @Override
                    public void accept(ComicsAndMore data) {
                        lstBXH.addAll(data.getComics());
                        lstUrl.addAll(data.getMoreUrls());
                        if (lstBXH.size() > 0) {
                            mNoPost.setVisibility(View.GONE);
                        } else {
                            mNoPost.setVisibility(View.VISIBLE);
                        }
                        onItemsLoadComplete();
                        adapterBXH.notifyDataSetChanged();
                    }
                });
//                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, Link.URL_MONTH, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Document document = Jsoup.parse(response);
//                        Elements all = document.select("div#ctl00_divCenter");
//                        Elements sub = all.select(".item");
//                        for (Element element : sub) {
//                            lstBXH.add(ParserUtils.getComic(element));
//                        }
//
//                        Elements more = document.select("div#ctl00_mainContent_ctl01_divPager");
//                        Elements more1 = more.select("ul.pagination");
//                        Elements allli = more1.select("li");
//                        Elements next = allli.select("a");
//                        for (Element element : next) {
//                            String url = element.attr("href");
//                            lstUrl.add(url);
//                        }
//
//                        if (lstBXH.size() > 0) {
//                            mNoPost.setVisibility(View.GONE);
//                        } else {
//                            mNoPost.setVisibility(View.VISIBLE);
//                        }
//
//                        onItemsLoadComplete();
//                        adapterBXH.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                });
//                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        150000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                requestQueue.add(stringRequest);
            }
        }).start();
    }

}
