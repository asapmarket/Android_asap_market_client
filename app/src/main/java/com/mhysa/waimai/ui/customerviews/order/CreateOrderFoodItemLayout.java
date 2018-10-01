package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.cart.CartFood;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/7
 *
 * @author xusheng
 */

public class CreateOrderFoodItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_quality)
    TextView mQualityTv;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    private CartFood mCartFood;

    public CreateOrderFoodItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public CreateOrderFoodItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_create_order_food_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setData(CartFood food) {
        if (null == food) {
            return;
        }

        mCartFood = food;
        try {
            mPriceTv.setText(mCartFood.price);
            mQualityTv.setText("*" + mCartFood.quality);
            if (!StringUtils.getInstance().isNullOrEmpty(mCartFood.spec_id_list)) {
                String[] specialNameEn = mCartFood.specials_name_en.split(",");
                String[] specialNameCn = mCartFood.specials_name_cn.split(",");

                StringBuilder stringBuilder = new StringBuilder();
                if (Utils.isChinese(mContext)) {
                    stringBuilder.append(mCartFood.foods_name_cn);
                    stringBuilder.append("_");
                    for (int i = 0; i < specialNameCn.length; i++) {
                        stringBuilder.append(specialNameCn[i]);
                        if (i != specialNameCn.length - 1) {
                            stringBuilder.append("_");
                        }
                    }
                } else {
                    stringBuilder.append(mCartFood.foods_name_en);
                    stringBuilder.append("_");
                    for (int i = 0; i < specialNameEn.length; i++) {
                        stringBuilder.append(specialNameEn[i]);
                        if (i != specialNameEn.length - 1) {
                            stringBuilder.append("_");
                        }
                    }
                }

                mNameTv.setText(stringBuilder.toString());
            } else {
                if (Utils.isChinese(mContext)) {
                    StringUtils.getInstance().setText(mCartFood.foods_name_cn, mNameTv);
                } else {
                    StringUtils.getInstance().setText(mCartFood.foods_name_en, mNameTv);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}
