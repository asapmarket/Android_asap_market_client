package com.mhysa.waimai.ui.activities.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.model.tab.TabInfo;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.widget.indicator.TitleIndicator;
import com.joey.devilfish.widget.indicator.ViewPagerCompat;
import com.mhysa.waimai.R;
import com.mhysa.waimai.ui.fragments.order.ErrandOrderListFragment;
import com.mhysa.waimai.ui.fragments.order.TakeoutOrderListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/8/14
 *
 * @author xusheng
 */

public class OrderListActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String INTENT_TITLE = "intent_title";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    private String mTitle;

    private static final int TAB_TAKEOUT = 0;

    private static final int TAB_ERRAND = 1;

    private int mCurrentTab = TAB_TAKEOUT;

    private TitleIndicator mTitleIndicator;

    private ViewPagerCompat mViewPager;

    private MyAdapter mAdapter;

    private List<TabInfo> mTabInfoList = new ArrayList<TabInfo>();

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mTitle = getIntent().getStringExtra(INTENT_TITLE);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_list;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(mTitle);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mViewPager = (ViewPagerCompat) mRootView.findViewById(R.id.vpc_order_content);
        mViewPager.setNoScroll(false);
        mTitleIndicator = (TitleIndicator) mRootView.findViewById(R.id.ti_order_title);
        bindTabList();
        bindViewPager();
    }

    private void bindTabList() {
        mTabInfoList.add(new TabInfo(TAB_TAKEOUT, getResources().getString(R.string.takeout_order)));
        mTabInfoList.add(new TabInfo(TAB_ERRAND, getResources().getString(R.string.errand_order)));
        mTitleIndicator.init(mCurrentTab, mTabInfoList, mViewPager, false);
    }

    private void bindViewPager() {
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(mTabInfoList.size());
        mViewPager.setCurrentItem(mCurrentTab);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTitleIndicator.onScrolled((mViewPager.getWidth() + mViewPager.getPageMargin()) * position + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mTitleIndicator.onSwitched(position);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            BaseFragment fragment = null;
            switch (pos) {
                case TAB_TAKEOUT:
                    TakeoutOrderListFragment firstFragment = new TakeoutOrderListFragment();
                    firstFragment.setStatus(FusionCode.OrderStatus.ORDER_STATUS_FINISHED);
                    fragment = firstFragment;
                    break;
                case TAB_ERRAND:
                    ErrandOrderListFragment secondFragment = new ErrandOrderListFragment();
                    secondFragment.setStatus(FusionCode.OrderStatus.ORDER_STATUS_FINISHED);
                    fragment = secondFragment;
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mTabInfoList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseFragment fragment = null;
            switch (position) {
                case TAB_TAKEOUT:
                    fragment = (TakeoutOrderListFragment) super.instantiateItem(container, position);
                    break;
                case TAB_ERRAND:
                    fragment = (ErrandOrderListFragment) super.instantiateItem(container, position);
                    break;
                default:
                    break;
            }

            return fragment;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @OnClick({R.id.layout_back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                OrderListActivity.this.finish();
                break;
            }

            default: {
                break;
            }
        }
    }

    public static void invoke(Context context, String title) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra(INTENT_TITLE, title);
        context.startActivity(intent);
    }
}
