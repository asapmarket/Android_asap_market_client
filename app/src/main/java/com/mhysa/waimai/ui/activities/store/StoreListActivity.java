package com.mhysa.waimai.ui.activities.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.NetworkUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.CircularProgress;
import com.joey.devilfish.widget.listview.XListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.model.store.StoreGroup;
import com.mhysa.waimai.model.store.StoreList;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.store.StoreListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class StoreListActivity extends BaseActivity {

    private static final String INTENT_TYPE = "intent_type";

    private static final String INTENT_NAME_CN = "intent_name_cn";

    private static final String INTENT_NAME_EN = "intent_name_en";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.lv_store)
    XListView mStoreLv;

    private StoreListAdapter mAdapter;

    private ArrayList<Store> mStores = new ArrayList<Store>();

    private ArrayList<StoreGroup> mStoreGroups = new ArrayList<StoreGroup>();

    private View mFooterView;

    private TextView mLoadMoreTv;

    private CircularProgress mLoadMoreProgress;

    private int mLastVisibleIndex;

    private boolean mIsLoadMore = false;

    private boolean mIsRefresh = false;

    private boolean mIsInit = false;

    private boolean mHasAddFooterView = false;

    private int mCurrentPage = FusionCode.PageConstant.INIT_PAGE;

    private String mNameCn;

    private String mNameEn;

    private String mType;

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mNameCn = getIntent().getStringExtra(INTENT_NAME_CN);
            mNameEn = getIntent().getStringExtra(INTENT_NAME_EN);
            mType = getIntent().getStringExtra(INTENT_TYPE);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_store_list;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        if (Utils.isChinese(this)) {
            mTitleTv.setText(StringUtils.getInstance().isNullOrEmpty(mNameCn)
                    ? "" : mNameCn);
        } else {
            mTitleTv.setText(StringUtils.getInstance().isNullOrEmpty(mNameEn)
                    ? "" : mNameEn);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mFooterView = LayoutInflater.from(this).inflate(R.layout.layout_load_more, null, false);
        mLoadMoreTv = (TextView) mFooterView.findViewById(R.id.tv_footer_text);
        mLoadMoreProgress = (CircularProgress) mFooterView.findViewById(R.id.progress);

        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });

        mStoreLv.setPullLoadEnable(false);
        mStoreLv.setPullRefreshEnable(true);
        mStoreLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && mLastVisibleIndex >= mAdapter.getCount() - 1) {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

        mStoreLv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {

            }
        });

        mAdapter = new StoreListAdapter(this, mStoreGroups);
        mStoreLv.setAdapter(mAdapter);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        showNetDialog("");
        mIsInit = true;
        getStoreList();
    }

    private void loadMore() {
        if (mIsLoadMore) {
            return;
        }

        if (mIsRefresh || mIsInit) {
            mLoadMoreProgress.stopSpinning();
            mLoadMoreProgress.setVisibility(View.GONE);
            mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            mLoadMoreProgress.setVisibility(View.VISIBLE);
            mLoadMoreProgress.startSpinning();
            mLoadMoreTv.setText(R.string.xlistview_header_hint_loading);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mLoadMoreProgress.stopSpinning();
                    mLoadMoreProgress.setVisibility(View.GONE);
                    mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
                    mIsLoadMore = false;
                }
            }, 100);
            return;
        }

        if (!mHasAddFooterView) {
            return;
        }

        mIsLoadMore = true;
        mLoadMoreProgress.setVisibility(View.VISIBLE);
        mLoadMoreProgress.startSpinning();
        mLoadMoreTv.setText(R.string.xlistview_header_hint_loading);

        getStoreList();
    }

    private void refresh() {
        if (mIsRefresh) {
            return;
        }

        if (mIsLoadMore || mIsInit) {
            mStoreLv.stopRefresh();
            return;
        }

        mIsRefresh = true;
        mCurrentPage = FusionCode.PageConstant.INIT_PAGE;
        getStoreList();
    }

    private void getStoreList() {
        OkHttpClientManager.getInstance().getStoreList(mCurrentPage, FusionCode.PageConstant.PAGE_SIZE, "", mType,
                new WtNetWorkListener<StoreList>() {
                    @Override
                    public void onSucess(RemoteReturnData<StoreList> data) {
                        if (null != data
                                && null != data.data
                                && null != data.data.rows
                                && data.data.rows.size() > 0) {
                            List<Store> stores = data.data.rows;
                            int page = data.data.total_page;
                            final int size = stores.size();
                            if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE) {
                                mStores.clear();
                            }

                            if (size < FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage > FusionCode.PageConstant.INIT_PAGE
                                    && mHasAddFooterView) {
                                mStoreLv.removeFooterView(mFooterView);
                                mHasAddFooterView = false;
                            }

                            if (size == FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage < page) {
                                if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE && !mHasAddFooterView) {
                                    mStoreLv.addFooterView(mFooterView);
                                    mHasAddFooterView = true;
                                }

                                mCurrentPage = mCurrentPage + 1;
                            }

                            addStores(stores);

                        } else {
                            if (mCurrentPage != FusionCode.PageConstant.INIT_PAGE && mHasAddFooterView) {
                                mStoreLv.removeFooterView(mFooterView);
                                mHasAddFooterView = false;
                            }
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(StoreListActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        mIsInit = false;
                        mIsLoadMore = false;
                        mIsRefresh = false;
                        closeNetDialog();
                        mStoreLv.stopLoadMore();
                        mStoreLv.stopRefresh();

                        mLoadMoreProgress.stopSpinning();
                        mLoadMoreProgress.setVisibility(View.GONE);
                        mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
                    }
                });
    }

    private void addStores(List<Store> stores) {
        if (null == stores || stores.size() == 0) {
            return;
        }

        mStores.addAll(stores);
        mStoreGroups.clear();

        final int size = mStores.size();

        if (size % 2 == 0) {
            for (int i = 0; i < size / 2; i++) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(i * 2);
                storeGroup.rightStore = mStores.get(i * 2 + 1);
                mStoreGroups.add(storeGroup);
            }
        } else {
            for (int i = 0; i < size / 2; i++) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(i * 2);
                storeGroup.rightStore = mStores.get(i * 2 + 1);
                mStoreGroups.add(storeGroup);
            }

            StoreGroup storeGroup = new StoreGroup();
            storeGroup.leftStore = mStores.get(size - 1);
            storeGroup.rightStore = null;
            mStoreGroups.add(storeGroup);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @OnClick({R.id.layout_back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                StoreListActivity.this.finish();
                break;
            }

            default: {
                break;
            }
        }
    }

    public static void invoke(Context context, String type, String nameCn, String nameEn) {
        Intent intent = new Intent(context, StoreListActivity.class);
        intent.putExtra(INTENT_TYPE, type);
        intent.putExtra(INTENT_NAME_CN, nameCn);
        intent.putExtra(INTENT_NAME_EN, nameEn);
        context.startActivity(intent);
    }
}
