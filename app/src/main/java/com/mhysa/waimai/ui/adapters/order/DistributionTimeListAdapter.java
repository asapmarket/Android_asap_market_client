package com.mhysa.waimai.ui.adapters.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.order.DistributionTime;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 * Date: 2018/3/10
 *
 * @author xusheng
 */

public class DistributionTimeListAdapter extends BaseAdapter {

    private Context mContext;

    private List<DistributionTime> mDatas = new ArrayList<DistributionTime>();

    public DistributionTimeListAdapter(Context context, List<DistributionTime> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {

        if (null != mDatas) {
            return mDatas.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mDatas && mDatas.size() > position) {
            return mDatas.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_distributiontime, null);

            holder.mDividerView = (View) convertView.findViewById(R.id.view_divider);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_distrbutiontime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DistributionTime time = mDatas.get(position);
        if (null != time) {
            holder.mTimeTv.setText(StringUtils.getInstance().isNullOrEmpty(time.distribution_time) ?
                    "" : time.distribution_time);

            holder.mDividerView.setVisibility(position == mDatas.size() - 1 ? View.GONE : View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTimeTv;
        View mDividerView;
    }
}
