package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * 文件描述
 * Date: 2017/8/1
 *
 * @author xusheng
 */

public class OrderDetailMapLayout extends LinearLayout {

    private PullToRefreshScrollView scrollView;

    public OrderDetailMapLayout(Context context) {
        super(context);
    }

    public OrderDetailMapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(PullToRefreshScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null == scrollView) {
            return false;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
