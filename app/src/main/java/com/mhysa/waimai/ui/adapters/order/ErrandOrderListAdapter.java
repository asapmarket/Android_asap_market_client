package com.mhysa.waimai.ui.adapters.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.errand.ErrandOrder;
import com.mhysa.waimai.ui.activities.errand.ErrandHomeActivity;
import com.mhysa.waimai.ui.activities.order.ErrandOrderDetailActivity;
import com.mhysa.waimai.ui.customerviews.addpicture.AddPictureLayout;
import com.mhysa.waimai.ui.customerviews.order.ErrandOrderScheduleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 * Date: 2018/3/15
 *
 * @author xusheng
 */

public class ErrandOrderListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ErrandOrder> mOrders = new ArrayList<ErrandOrder>();

    private int mWidth;

    private int mHeight;

    public ErrandOrderListAdapter(Context context, List<ErrandOrder> orders) {
        this.mContext = context;
        this.mOrders = orders;

        int dpToPx = ExtendUtils.getInstance().dip2px(mContext, 10);
        mWidth = (AppConfig.getScreenWidth() - (ErrandHomeActivity.NUM_IN_LINE + 1) * dpToPx) / ErrandHomeActivity.NUM_IN_LINE;
        mHeight = mWidth;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_errand_order, null);

            holder.mOrderIdTv = (TextView) convertView.findViewById(R.id.tv_order_id);
            holder.mRemarkTv = (TextView) convertView.findViewById(R.id.tv_remark);
            holder.mPictureLayout = (AddPictureLayout) convertView.findViewById(R.id.layout_images);
            holder.mOrderScheduleLayout = (ErrandOrderScheduleLayout) convertView.findViewById(R.id.layout_order_schedule);
            holder.mViewDetailTv = (TextView) convertView.findViewById(R.id.tv_view_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ErrandOrder order = mOrders.get(position);
        if (null != order) {
            holder.mOrderIdTv.setText(StringUtils.getInstance().isNullOrEmpty(order.order_id) ?
                    "" : order.order_id);
            StringUtils.getInstance().setText(order.remark, holder.mRemarkTv);

            if (null != order.imgs && order.imgs.size() > 0) {
                holder.mPictureLayout.setVisibility(View.VISIBLE);
                holder.mPictureLayout.setNumInLine(ErrandHomeActivity.NUM_IN_LINE);
                holder.mPictureLayout.setLimitSize(ErrandHomeActivity.LIMIT_SIZE);
                holder.mPictureLayout.setIsView(true);
                holder.mPictureLayout.setDimensions(mWidth, mHeight);
                holder.mPictureLayout.addPictures(order.imgs);
            } else {
                holder.mPictureLayout.setVisibility(View.GONE);
            }

            holder.mOrderScheduleLayout.setStatus(order);
            holder.mViewDetailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看订单详情
                    ErrandOrderDetailActivity.invoke(mContext, order.order_id);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        TextView mOrderIdTv;
        TextView mRemarkTv;
        AddPictureLayout mPictureLayout;
        ErrandOrderScheduleLayout mOrderScheduleLayout;
        TextView mViewDetailTv;
    }
}
