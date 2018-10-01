package com.mhysa.waimai.ui.adapters.slide;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * 文件描述
 * Date: 2017/8/25
 *
 * @author xusheng
 */

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> mViews;

    public ViewPagerAdapter(ArrayList<View> views) {
        this.mViews = views;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(mViews.get(position));
    }

    @Override
    public int getCount() {
        if (null != mViews) {
            return mViews.size();
        }

        return 0;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(mViews.get(position), 0);
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == (View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
