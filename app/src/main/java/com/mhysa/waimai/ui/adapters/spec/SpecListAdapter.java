package com.mhysa.waimai.ui.adapters.spec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.special.Special;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 * Date: 2017/8/4
 *
 * @author xusheng
 */

public class SpecListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Special> mSpecials = new ArrayList<Special>();

    private int mSelectedPosition = 0;

    public SpecListAdapter(Context context, List<Special> specials) {
        this.mContext = context;
        this.mSpecials = specials;
    }

    public void setSelectedPosition(int position) {
        this.mSelectedPosition = position;
        this.notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    @Override
    public int getCount() {

        if (null != mSpecials) {
            return mSpecials.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mSpecials && mSpecials.size() > position) {
            return mSpecials.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_spec, null);

            holder.mSpecItemLayout = (RelativeLayout) convertView.findViewById(R.id.layout_spec_item);
            holder.mSpecItemTv = (TextView) convertView.findViewById(R.id.tv_spec_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Special special = mSpecials.get(position);
        if (null != special) {
            if (Utils.isChinese(mContext)) {
                StringUtils.getInstance().setText(special.spec_name_cn, holder.mSpecItemTv);
            } else {
                StringUtils.getInstance().setText(special.spec_name_en, holder.mSpecItemTv);
            }

            if (position == mSelectedPosition) {
                holder.mSpecItemLayout.setBackgroundResource(R.mipmap.ic_spec_selected);
            } else {
                holder.mSpecItemLayout.setBackgroundResource(R.mipmap.ic_spec_normal);
            }
        }

        return convertView;
    }

    class ViewHolder {
        RelativeLayout mSpecItemLayout;
        TextView mSpecItemTv;
    }
}
