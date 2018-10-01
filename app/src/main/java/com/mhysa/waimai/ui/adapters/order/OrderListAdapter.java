package com.mhysa.waimai.ui.adapters.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.ui.activities.order.OrderDetailActivity;
import com.mhysa.waimai.ui.customerviews.order.OrderListStoreItemLayout;
import com.mhysa.waimai.ui.customerviews.order.OrderScheduleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class OrderListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Order> mOrders = new ArrayList<Order>();

    private LinearLayout.LayoutParams mStoreParams;

    public OrderListAdapter(Context context, List<Order> orders) {
        this.mContext = context;
        this.mOrders = orders;
        this.mStoreParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mStoreParams.topMargin = ExtendUtils.getInstance().dip2px(mContext, 10);
    }

    @Override
    public int getCount() {

        if (null != mOrders) {
            return mOrders.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mOrders && mOrders.size() > position) {
            return mOrders.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_order, null);

            holder.mOrderIdTv = (TextView) convertView.findViewById(R.id.tv_order_id);
            holder.mStoreLayout = (LinearLayout) convertView.findViewById(R.id.layout_store);
            holder.mOrderScheduleLayout = (OrderScheduleLayout) convertView.findViewById(R.id.layout_order_schedule);
            holder.mViewDetailTv = (TextView) convertView.findViewById(R.id.tv_view_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Order order = mOrders.get(position);
        if (null != order) {
            holder.mOrderIdTv.setText(StringUtils.getInstance().isNullOrEmpty(order.order_id) ?
                    "" : order.order_id);

            if (null != order.store_list && order.store_list.size() > 0) {
                holder.mStoreLayout.setVisibility(View.VISIBLE);
                holder.mStoreLayout.removeAllViews();

                final int size = order.store_list.size();
                for (int i = 0; i < size; i++) {
                    OrderListStoreItemLayout layout = new OrderListStoreItemLayout(mContext);
                    layout.setData(order.store_list.get(i));
                    holder.mStoreLayout.addView(layout, mStoreParams);
                }
            } else {
                holder.mStoreLayout.setVisibility(View.GONE);
                holder.mStoreLayout.removeAllViews();
            }

            holder.mOrderScheduleLayout.setStatus(false, order.state, order.exp_name);
            holder.mViewDetailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看订单详情
                    OrderDetailActivity.invoke(mContext, order.order_id);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        TextView mOrderIdTv;
        LinearLayout mStoreLayout;
        OrderScheduleLayout mOrderScheduleLayout;
        TextView mViewDetailTv;
    }
}
