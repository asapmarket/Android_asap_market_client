package com.mhysa.waimai.ui.customerviews.scrollloop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * ScrollView中嵌套ViewPager
 * Created by zhangzehao on 2014/11/13/0013.
 */
public class ViewPagerScrollView extends ScrollView {

    private float xDistance, yDistance, xLast, yLast;


    public ViewPagerScrollView(Context context) {
        super(context);
    }

    public ViewPagerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
