package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.store.Store;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 订单详情商家item
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class OrderDetailStoreItemLayout extends RelativeLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_quality)
    TextView mQualityTv;

    @Bind(R.id.layout_foods)
    LinearLayout mFoodLayout;

    public OrderDetailStoreItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public OrderDetailStoreItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_order_detail_store_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setData(Store store) {
        if (null == store) {
            return;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ExtendUtils.getInstance().dip2px(mContext, 10);
        if (Utils.isChinese(mContext)) {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_cn) ?
                    "" : store.store_name_cn);
        } else {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_en) ?
                    "" : store.store_name_en);
        }

        mQualityTv.setText(mContext.getString(R.string.goods_count, "" + store.foods_count));

        mFoodLayout.removeAllViews();
        if (null != store.foods_list
                && store.foods_list.size() > 0) {
            final int size = store.foods_list.size();
            for (int i = 0; i < size; i++) {
                OrderDetailFoodItemLayout layout = new OrderDetailFoodItemLayout(mContext);
                layout.setData(store.foods_list.get(i));
                mFoodLayout.addView(layout, params);
            }
        }
    }
}