package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.cart.CartFood;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/7
 *
 * @author xusheng
 */

public class CreateOrderStoreItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.sdv_store_image)
    SimpleDraweeView mStoreSdv;

    @Bind(R.id.tv_store_name)
    TextView mStoreNameTv;

    @Bind(R.id.layout_foods)
    LinearLayout mFoodsLayout;

    @Bind(R.id.tv_total_price)
    TextView mTotalPriceTv;

    private BigDecimal mPrice;

    public CreateOrderStoreItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public CreateOrderStoreItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_create_order_store_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public BigDecimal setData(List<CartFood> cartFoods) {
        if (null == cartFoods || cartFoods.size() == 0) {
            return null;
        }

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ExtendUtils.getInstance().dip2px(mContext, 20);

        try {
            if (Utils.isChinese(mContext)) {
                StringUtils.getInstance().setText(cartFoods.get(0).store_name_cn, mStoreNameTv);
                ImageUtils.getInstance().setImageURL(cartFoods.get(0).store_image_cn, mStoreSdv);
            } else {
                StringUtils.getInstance().setText(cartFoods.get(0).store_name_en, mStoreNameTv);
                ImageUtils.getInstance().setImageURL(cartFoods.get(0).store_image_en, mStoreSdv);
            }

            final int size = cartFoods.size();
            mPrice = new BigDecimal(0.0);
            for (int i = 0; i < size; i++) {
                CartFood cartFood = cartFoods.get(i);

                if (!StringUtils.getInstance().isNullOrEmpty(cartFood.price)) {
                    BigDecimal childPrice = BigDecimal.valueOf(Double.valueOf(cartFood.price));
                    for (int j = 0; j < cartFood.quality; j++) {
                        mPrice = mPrice.add(childPrice);
                    }
                }

                CreateOrderFoodItemLayout layout = new CreateOrderFoodItemLayout(mContext);
                layout.setData(cartFood);
                mFoodsLayout.addView(layout, params);
            }

            mTotalPriceTv.setText("$" + mPrice.toPlainString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return mPrice;
    }
}
