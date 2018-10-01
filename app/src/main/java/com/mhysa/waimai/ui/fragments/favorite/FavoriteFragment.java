package com.mhysa.waimai.ui.fragments.favorite;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.utils.NetworkUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.CircularProgress;
import com.joey.devilfish.widget.listview.XListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.model.store.StoreGroup;
import com.mhysa.waimai.model.store.StoreList;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.store.StoreListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 文件描述
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class FavoriteFragment extends BaseFragment {

    @Bind(R.id.lv_store)
    XListView mStoreLv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

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

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_favorite;
    }

    @Override
    protected void initContentView() {
        super.initContentView();

        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_more, null, false);
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
                        && mLastVisibleIndex >= mAdapter.getCount() - 1 && mHasAddFooterView) {
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

        mAdapter = new StoreListAdapter(getActivity(), mStoreGroups);
        mStoreLv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);

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

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
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
        OkHttpClientManager.getInstance().getFavoriteStoreList(Utils.getUserId(getActivity())
                , Utils.getToken(getActivity()), mCurrentPage, FusionCode.PageConstant.PAGE_SIZE,
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

                            if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE) {
                                mStores.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(mContext, status);
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

    private void removeStore(Store store) {
        if (null == store || !mStores.contains(store)) {
            return;
        }

        mStores.remove(store);
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
    public void onResume() {
        super.onResume();
        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        if (null == event) {
            return;
        }

        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.DELETE_STORE_FAVORITE: {
                if (null != event.getData()) {
                    if (mStores.contains(event.getData())) {
                        removeStore((Store) event.getData());
                    }
                }
                break;
            }

            case EventID.ADD_STORE_FAVORITE: {
                if (null != event.getData()) {
                    if (!mStores.contains(event.getData())) {
                        List<Store> stores = new ArrayList<Store>();
                        stores.add(0, (Store) event.getData());
                        addStores(stores);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
