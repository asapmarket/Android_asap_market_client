package com.mhysa.waimai.ui.customerviews.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.joey.devilfish.utils.ExtendUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.food.Food;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class HomeHotLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.layout_content)
    LinearLayout mContentLayout;

    public HomeHotLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public HomeHotLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_home_hot, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setData(List<Food> foods) {
        if (null == foods || foods.size() == 0) {
            return;
        }

        mContentLayout.removeAllViews();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ExtendUtils.getInstance().dip2px(mContext, 10);

        final int size = foods.size();

        for (int i = 0; i < size; i++) {
            HomeHotItemLayout layout = new HomeHotItemLayout(mContext);
            layout.setData(foods.get(i));

            mContentLayout.addView(layout, params);
        }
    }
}
