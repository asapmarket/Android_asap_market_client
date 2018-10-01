package com.mhysa.waimai.ui.adapters.store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.store.FoodType;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类列表adapter
 * Date: 2017/8/2
 *
 * @author xusheng
 */

public class FoodTypeAdapter extends BaseAdapter {

    private Context mContext;

    private List<FoodType> mFoodTypes = new ArrayList<FoodType>();

    public FoodTypeAdapter(Context context, List<FoodType> types) {
        this.mContext = context;
        this.mFoodTypes = types;
    }

    @Override
    public int getCount() {

        if (null != mFoodTypes) {
            return mFoodTypes.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mFoodTypes && mFoodTypes.size() > position) {
            return mFoodTypes.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_food_type, null);

            holder.mTypeLayout = (LinearLayout) convertView.findViewById(R.id.layout_type);
            holder.mSectionTv = (TextView) convertView.findViewById(R.id.tv_section);
            holder.mTypeTv = (TextView) convertView.findViewById(R.id.tv_type);
            holder.mDividerView = (View) convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mSectionTv.setText(position + "");

        final FoodType type = mFoodTypes.get(position);
        if (null != type) {

            holder.mDividerView.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

            if (Utils.isChinese(mContext)) {
                StringUtils.getInstance().setText(type.type_name_cn, holder.mTypeTv);
            } else {
                StringUtils.getInstance().setText(type.type_name_en, holder.mTypeTv);
            }

        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout mTypeLayout;
        TextView mSectionTv;
        TextView mTypeTv;
        View mDividerView;
    }
}
