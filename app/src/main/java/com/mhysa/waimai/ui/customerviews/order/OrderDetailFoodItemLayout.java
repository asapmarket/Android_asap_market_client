package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.food.Food;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 订单详情食品item
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class OrderDetailFoodItemLayout extends RelativeLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.tv_quality)
    TextView mQualityTv;

    @Bind(R.id.tv_state)
    TextView mStateTv;

    public OrderDetailFoodItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public OrderDetailFoodItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_order_detail_food_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setData(Food food) {
        if (null == food) {
            return;
        }

        if (Utils.isChinese(mContext)) {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(food.foods_name_cn) ?
                    "" : food.foods_name_cn);
        } else {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(food.foods_name_en) ?
                    "" : food.foods_name_en);
        }

        if (!StringUtils.getInstance().isNullOrEmpty(food.price)) {
            mPriceTv.setText(mContext.getString(R.string.food_price, food.price));
        }

        mQualityTv.setText(mContext.getString(R.string.food_quality, food.foods_quantity + ""));

        if (StringUtils.getInstance().isNullOrEmpty(food.state)) {
            return;
        }

        if (food.state.equals(FusionCode.FoodPickupState.STATE_ALREADY_PICKUP)) {
            mStateTv.setText(R.string.pickup_success);
        } else {
            mStateTv.setText(R.string.pickup_no);
        }
    }
}
