package com.mhysa.waimai.ui.fragments.order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.model.tab.TabInfo;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.widget.indicator.TitleIndicator;
import com.joey.devilfish.widget.indicator.ViewPagerCompat;
import com.mhysa.waimai.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 文件描述
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class OrderFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private static final int TAB_TAKEOUT = 0;

    private static final int TAB_ERRAND = 1;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    private int mCurrentTab = TAB_TAKEOUT;

    private TitleIndicator mTitleIndicator;

    private ViewPagerCompat mViewPager;

    private MyAdapter mAdapter;

    private List<TabInfo> mTabInfoList = new ArrayList<TabInfo>();

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initContentView() {
        super.initContentView();

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
        mAdapter = new MyAdapter(getActivity().getSupportFragmentManager());
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
                    firstFragment.setStatus(FusionCode.OrderStatus.ORDER_STATUS_ALL);
                    fragment = firstFragment;
                    break;
                case TAB_ERRAND:
                    ErrandOrderListFragment secondFragment = new ErrandOrderListFragment();
                    secondFragment.setStatus(FusionCode.OrderStatus.ORDER_STATUS_ALL);
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
}
