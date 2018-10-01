package com.mhysa.waimai.ui.fragments.order;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.model.order.OrderList;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.order.OrderListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 文件描述
 * Date: 2018/3/13
 *
 * @author xusheng
 */

public class TakeoutOrderListFragment extends BaseFragment {

    @Bind(R.id.lv_takeout_order)
    XListView mOrderLv;

    private OrderListAdapter mAdapter;

    private List<Order> mOrders = new ArrayList<Order>();

    private View mFooterView;

    private TextView mLoadMoreTv;

    private CircularProgress mLoadMoreProgress;

    private int mLastVisibleIndex;

    private boolean mIsLoadMore = false;

    private boolean mIsRefresh = false;

    private boolean mIsInit = false;

    private boolean mHasAddFooterView = false;

    private int mCurrentPage = FusionCode.PageConstant.INIT_PAGE;

    private String mStatus;

    public void setStatus(String status) {
        this.mStatus = status;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_takeout_order_list;
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

        mOrderLv.setPullLoadEnable(false);
        mOrderLv.setPullRefreshEnable(true);
        mOrderLv.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        mOrderLv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {

            }
        });

        mAdapter = new OrderListAdapter(getActivity(), mOrders);
        mOrderLv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();

        EventBus.getDefault().register(this);

        showNetDialog("");
        mIsInit = true;
        getOrderList();
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

        getOrderList();
    }

    private void refresh() {
        if (mIsRefresh) {
            return;
        }

        if (mIsLoadMore || mIsInit) {
            mOrderLv.stopRefresh();
            return;
        }

        mIsRefresh = true;
        mCurrentPage = FusionCode.PageConstant.INIT_PAGE;
        getOrderList();
    }

    private void getOrderList() {
        OkHttpClientManager.getInstance().getOrderList(Utils.getUserId(getActivity())
                , Utils.getToken(getActivity()), mCurrentPage,
                FusionCode.PageConstant.PAGE_SIZE, mStatus + "",
                new WtNetWorkListener<OrderList>() {
                    @Override
                    public void onSucess(RemoteReturnData<OrderList> data) {
                        if (null != data
                                && null != data.data
                                && null != data.data.rows
                                && data.data.rows.size() > 0) {
                            List<Order> orders = data.data.rows;
                            int page = data.data.total_page;
                            final int size = orders.size();
                            if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE) {
                                mOrders.clear();
                            }

                            if (size < FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage > FusionCode.PageConstant.INIT_PAGE
                                    && mHasAddFooterView) {
                                mOrderLv.removeFooterView(mFooterView);
                                mHasAddFooterView = false;
                            }

                            if (size == FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage < page) {
                                if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE && !mHasAddFooterView) {
                                    mOrderLv.addFooterView(mFooterView);
                                    mHasAddFooterView = true;
                                }

                                mCurrentPage = mCurrentPage + 1;
                            }

                            mOrders.addAll(orders);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            if (mCurrentPage != FusionCode.PageConstant.INIT_PAGE && mHasAddFooterView) {
                                mOrderLv.removeFooterView(mFooterView);
                                mHasAddFooterView = false;
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
                        mOrderLv.stopLoadMore();
                        mOrderLv.stopRefresh();

                        mLoadMoreProgress.stopSpinning();
                        mLoadMoreProgress.setVisibility(View.GONE);
                        mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        if (null == event) {
            return;
        }

        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.PAY_SUCCESS: {
                refresh();
                break;
            }
        }
    }
}
