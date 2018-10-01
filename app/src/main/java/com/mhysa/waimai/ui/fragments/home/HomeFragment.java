package com.mhysa.waimai.ui.fragments.home;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.fusion.FusionField;
import com.joey.devilfish.fusion.SharedPreferenceConstant;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.utils.NetworkUtils;
import com.joey.devilfish.utils.SharedPreferenceUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.CircularProgress;
import com.joey.devilfish.widget.listview.XListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.home.HomeInfo;
import com.mhysa.waimai.model.home.Type;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.model.store.StoreGroup;
import com.mhysa.waimai.model.store.StoreList;
import com.mhysa.waimai.ui.activities.errand.ErrandHomeActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.main.MainActivity;
import com.mhysa.waimai.ui.adapters.store.StoreListAdapter;
import com.mhysa.waimai.ui.customerviews.home.HomeHeaderLayout;
import com.mhysa.waimai.ui.customerviews.home.HomeTypeLayoutV2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class HomeFragment extends BaseFragment {

    private HomeHeaderLayout mHeaderLayout;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHomeHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.lv_store)
    XListView mStoreLv;

    @Bind(R.id.tv_language)
    TextView mLanguageTv;

    @Bind(R.id.layout_floating_errand)
    LinearLayout mErrandLayout;

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

    private int mLastLanguage = -1;

    private Type mType;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initContentView() {
        super.initContentView();

        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_more, null, false);
        mLoadMoreTv = (TextView) mFooterView.findViewById(R.id.tv_footer_text);
        mLoadMoreProgress = (CircularProgress) mFooterView.findViewById(R.id.progress);

        mHeaderLayout = new HomeHeaderLayout(getActivity());
        mHeaderLayout.setBackgroundColor(Color.parseColor("#f5f5f5"));
        mStoreLv.addHeaderView(mHeaderLayout);

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

                int top = 0;
                if (null != mHeaderLayout) {
                    top = mHeaderLayout.getTop();
                }

//                int height = (int) (AppConfig.getScreenWidth() * HomeHeaderLayout.CAROUSEL_RATIO) -
//                        ExtendUtils.getInstance().dip2px(mContext, 50);
                int height = (int) (AppConfig.getScreenWidth() * HomeHeaderLayout.CAROUSEL_RATIO);

                scrollChanged(top, height);
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

    private void scrollChanged(int top, int height) {
        if (top <= -height) {
            mErrandLayout.setVisibility(View.VISIBLE);
        } else {
            mErrandLayout.setVisibility(View.GONE);
        }

//        if (top >= 0) {
//            if (top == 0) {
//                mHomeHeaderLayout.getBackground().setAlpha(0);
//                mViewDivider.getBackground().setAlpha(0);
//                mTitleTv.setTextColor(Color.argb(255, 255, 255, 255));
//                mLanguageTv.setTextColor(Color.argb(255, 255, 255, 255));
//            } else {
//                mHomeHeaderLayout.getBackground().setAlpha(0);
//                mViewDivider.getBackground().setAlpha(0);
//                mTitleTv.setTextColor(Color.argb(0, 0, 0, 0));
//                mLanguageTv.setTextColor(Color.argb(0, 0, 0, 0));
//            }
//
//        } else {
//            if (top + height > 0) {
//                mHomeHeaderLayout.getBackground().setAlpha((-top) * 255 / height);
//                mViewDivider.getBackground().setAlpha((-top) * 255 / height);
//                mTitleTv.setTextColor(Color.argb((-top) * 255 / height, 26, 26, 26));
//                mLanguageTv.setTextColor(Color.argb((-top) * 255 / height, 26, 26, 26));
//            } else {
//                mHomeHeaderLayout.getBackground().setAlpha(255);
//                mViewDivider.getBackground().setAlpha(255);
//                mTitleTv.setTextColor(Color.argb(255, 26, 26, 26));
//                mLanguageTv.setTextColor(Color.argb(255, 26, 26, 26));
//            }
//        }
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        showNetDialog("");
        getHomeInfo();
        mIsInit = true;
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
        OkHttpClientManager.getInstance().getStoreList(mCurrentPage, FusionCode.PageConstant.PAGE_SIZE_FOR_HOME, "0",
                null != mType && !StringUtils.getInstance().isNullOrEmpty(mType.type_id) ? mType.type_id : "",
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

                            if (size < FusionCode.PageConstant.PAGE_SIZE_FOR_HOME
                                    && mCurrentPage > FusionCode.PageConstant.INIT_PAGE
                                    && mHasAddFooterView) {
                                mStoreLv.removeFooterView(mFooterView);
                                mHasAddFooterView = false;
                            }

                            if (size == FusionCode.PageConstant.PAGE_SIZE_FOR_HOME
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
                                mStoreGroups.clear();
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

        if (size % 3 == 0) {
            for (int i = 0; i < size / 3; i++) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(i * 3);
                storeGroup.middleStore = mStores.get(i * 3 + 1);
                storeGroup.rightStore = mStores.get(i * 3 + 2);
                mStoreGroups.add(storeGroup);
            }
        } else {
            for (int i = 0; i < size / 3; i++) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(i * 3);
                storeGroup.middleStore = mStores.get(i * 3 + 1);
                storeGroup.rightStore = mStores.get(i * 3 + 2);
                mStoreGroups.add(storeGroup);
            }

            if (size % 3 == 1) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(size - 1);
                storeGroup.middleStore = null;
                storeGroup.rightStore = null;
                mStoreGroups.add(storeGroup);
            }
            if (size % 3 == 2) {
                StoreGroup storeGroup = new StoreGroup();
                storeGroup.leftStore = mStores.get(size - 2);
                storeGroup.middleStore = mStores.get(size - 1);
                storeGroup.rightStore = null;
                mStoreGroups.add(storeGroup);
            }

        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取首页数据
     */
    private void getHomeInfo() {
        OkHttpClientManager.getInstance().getHomeInfo(new WtNetWorkListener<HomeInfo>() {
            @Override
            public void onSucess(RemoteReturnData<HomeInfo> data) {
                if (null != data && null != data.data) {
                    mHeaderLayout.setData(data.data);
                    mHeaderLayout.setListener(new HomeTypeLayoutV2.OnTypeSelectedListener() {
                        @Override
                        public void onTypeSelectd(Type type) {
                            mType = type;
                            refresh();
                        }
                    });

                    if (null != data.data.type_list
                            && data.data.type_list.size() > 0) {
                        mType = data.data.type_list.get(0);
                        getStoreList();
                    }
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    @OnClick({R.id.tv_language, R.id.layout_floating_errand})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tv_language: {
                if (mLastLanguage == FusionCode.LocalState.LOCAL_STATE_CHINESE) {
                    mLastLanguage = FusionCode.LocalState.LOCAL_STATE_ENGLISH;
                    mLanguageTv.setText("简体中文");
                } else {
                    mLastLanguage = FusionCode.LocalState.LOCAL_STATE_CHINESE;
                    mLanguageTv.setText("ENG");
                }

                FusionField.mCurrentLanguage = mLastLanguage;

                SharedPreferenceUtils.setSharedPreferences(SharedPreferenceConstant.PREFERENCE_NAME,
                        SharedPreferenceConstant.PROPERTY_LANGUAGE, mLastLanguage, getActivity());

                EventBus.getDefault().post(new ApplicationEvent(EventID.CHANGE_LANGUAGE_SUCCESS));
                MainActivity.invoke(getActivity());
                break;
            }

            case R.id.layout_floating_errand: {
                if (StringUtils.getInstance().isNullOrEmpty(Utils.getToken(mContext))) {
                    LoginActivity.invoke(mContext, false);
                } else {
                    ErrandHomeActivity.forward(mContext);
                }
                break;
            }

            default: {
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();

        mLastLanguage = SharedPreferenceUtils.getSharedPreferences(
                SharedPreferenceConstant.PREFERENCE_NAME, SharedPreferenceConstant.PROPERTY_LANGUAGE,
                getActivity(), FusionCode.LocalState.LOCAL_STATE_CHINESE);

        if (mLastLanguage == FusionCode.LocalState.LOCAL_STATE_CHINESE) {
            mLanguageTv.setText("ENG");
        } else {
            mLanguageTv.setText("简体中文");
        }
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
    }
}
