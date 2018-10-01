/**
 * Copyright (C) 2006-2014 Tuniu All rights reserved
 */
package com.mhysa.waimai.ui.customerviews.scrollloop;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.home.Banner;
import com.mhysa.waimai.ui.activities.h5.H5Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动播放
 * Date: 15/7/1
 *
 * @author xusheng2
 */
public class AutoScrollPlayView extends RelativeLayout {
    private Context mContext;
    private AutoScrollLoopViewPager mAutoScrollLoopViewPager;
    private CirclePageIndicator mCirclePageIndicator;
    private MyAutoPageAdapter myAutoScrollPlayViewPageAdapter;


    public AutoScrollPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AutoScrollPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAutoScrollLoopViewPager = (AutoScrollLoopViewPager) findViewById(R.id.pager_view);
        mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_two);
    }

    public void bindAutoScrollPlayViewData(List<Banner> bannerInfos, float ratio) {
        if (null == bannerInfos || bannerInfos.isEmpty()) {
            setVisibility(View.GONE);
            return;
        }
        for (Banner bannerInfo : bannerInfos) {
            if (null == bannerInfo) {
                bannerInfos.remove(bannerInfo);
            }
        }
        if (bannerInfos.size() == 1) {
            mCirclePageIndicator.setVisibility(View.GONE);
        } else {
            mCirclePageIndicator.setVisibility(View.VISIBLE);
        }

        myAutoScrollPlayViewPageAdapter = new MyAutoPageAdapter(bannerInfos, ratio);
        mAutoScrollLoopViewPager.setAdapter(myAutoScrollPlayViewPageAdapter);
        mCirclePageIndicator.setViewPager(mAutoScrollLoopViewPager);
        mAutoScrollLoopViewPager.setInterval(3000);
        if (null != bannerInfos && bannerInfos.size() > 1) {
            mAutoScrollLoopViewPager.startAutoScroll();
        }
    }

    public void startAutoScroll() {
        mAutoScrollLoopViewPager.startAutoScroll();
    }

    public void stopAutoScroll() {
        mAutoScrollLoopViewPager.stopAutoScroll();
    }

    private class MyAutoPageAdapter extends PagerAdapter {
        private int mWidth;
        private int mHeight;
        private ArrayList<Banner> mList = new ArrayList<Banner>();
        private LayoutInflater mInflater;
        private float mRatio;

        public MyAutoPageAdapter(List<Banner> list, float ratio) {
            mList.addAll(list);
            mRatio = ratio;
            mInflater = LayoutInflater.from(mContext);
            mWidth = AppConfig.getScreenWidth();
            mHeight = (int) (mWidth * mRatio);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Banner bannerInfo = mList.get(position);
            View userLayout = mInflater.inflate(R.layout.list_item_home_auto_scroll, container, false);
            SimpleDraweeView view = (SimpleDraweeView) userLayout.findViewById(R.id.sdv_autoscroll);

            view.setAspectRatio(mRatio);

            if (null != bannerInfo && !StringUtils.getInstance().isNullOrEmpty(bannerInfo.image)) {
                ImageUtils.getInstance().setImageURL(bannerInfo.image, view);
            }

            (container).addView(userLayout);

            userLayout.setTag(bannerInfo);
            userLayout.setOnClickListener(new CarouselListener(position));
            return userLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class CarouselListener implements OnClickListener {

        private int mPosition;

        public CarouselListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            Banner bannerInfo = (Banner) view.getTag();
            if (null == bannerInfo) {
                return;
            }

            if (!StringUtils.getInstance().isNullOrEmpty(bannerInfo.url)) {
                // 打开H5
                H5Activity.invoke(mContext, getResources().getString(R.string.view_detail), bannerInfo.url);
            }
        }
    }


}
