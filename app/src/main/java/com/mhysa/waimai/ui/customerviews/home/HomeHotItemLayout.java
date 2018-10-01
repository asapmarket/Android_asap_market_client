package com.mhysa.waimai.ui.customerviews.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.ui.activities.food.FoodDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class HomeHotItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.sdv_icon)
    SimpleDraweeView mIconSdv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    private int mImageWidth;

    private int mImageHeight;

    public HomeHotItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public HomeHotItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_home_hot_item, null);

        ButterKnife.bind(this, mRootView);

        mImageWidth = (int) ((AppConfig.getScreenWidth() -
                4 * ExtendUtils.getInstance().dip2px(mContext, 10)) / 3.5);
        mImageHeight = mImageWidth;

        LayoutParams layoutParams = new LayoutParams(mImageWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LayoutParams imageParams = new LayoutParams(mImageWidth,
                mImageHeight);

        mIconSdv.setLayoutParams(imageParams);

        this.addView(mRootView, layoutParams);
    }

    public void setData(final Food food) {
        if (null == food) {
            return;
        }

        if (Utils.isChinese(mContext)) {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(food.foods_name_cn)
                    ? "" : food.foods_name_cn);
            ImageUtils.getInstance().setImageURL(food.foods_image_cn, mIconSdv);
        } else {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(food.foods_name_en)
                    ? "" : food.foods_name_en);
            ImageUtils.getInstance().setImageURL(food.foods_image_en, mIconSdv);
        }

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodDetailActivity.invoke(mContext, food);
            }
        });

    }
}
