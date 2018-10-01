package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.store.Store;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 订单列表中，商家列表item
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class OrderListStoreItemLayout extends RelativeLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_count)
    TextView mCountTv;

    public OrderListStoreItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public OrderListStoreItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_order_list_store_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setData(Store store) {
        if (null == store) {
            return;
        }

        mCountTv.setText(mContext.getString(R.string.goods_count, store.quantity + ""));
        if (Utils.isChinese(mContext)) {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_cn)
                    ? "" : store.store_name_cn);
        } else {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_en)
                    ? "" : store.store_name_en);
        }
    }
}
