package com.mhysa.waimai.ui.adapters.store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mhysa.waimai.R;
import com.mhysa.waimai.model.store.StoreGroup;
import com.mhysa.waimai.ui.customerviews.home.StoreItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class StoreListAdapter extends BaseAdapter {

    private Context mContext;

    private List<StoreGroup> mStoreGroups = new ArrayList<StoreGroup>();

    public StoreListAdapter(Context context, List<StoreGroup> storeGroups) {
        this.mContext = context;
        this.mStoreGroups = storeGroups;
    }

    @Override
    public int getCount() {

        if (null != mStoreGroups) {
            return mStoreGroups.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mStoreGroups && mStoreGroups.size() > position) {
            return mStoreGroups.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_favorite_store, null);

            holder.mTopLayout = (LinearLayout) convertView.findViewById(R.id.layout_top);
            holder.mLeftItemLayout = (StoreItemLayout) convertView.findViewById(R.id.layout_store_left);
            holder.mMiddleItemLayout = (StoreItemLayout) convertView.findViewById(R.id.layout_store_middle);
            holder.mRightItemLayout = (StoreItemLayout) convertView.findViewById(R.id.layout_store_right);
            holder.mBottomLayout = (RelativeLayout) convertView.findViewById(R.id.layout_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StoreGroup storeGroup = mStoreGroups.get(position);
        if (null != storeGroup) {
            if (null != storeGroup.leftStore) {
                holder.mLeftItemLayout.setVisibility(View.VISIBLE);
                holder.mLeftItemLayout.setData(storeGroup.leftStore);
            } else {
                holder.mLeftItemLayout.setVisibility(View.INVISIBLE);
            }

            if (null != storeGroup.middleStore) {
                holder.mMiddleItemLayout.setVisibility(View.VISIBLE);
                holder.mMiddleItemLayout.setData(storeGroup.middleStore);
            } else {
                holder.mMiddleItemLayout.setVisibility(View.INVISIBLE);
            }

            if (null != storeGroup.rightStore) {
                holder.mRightItemLayout.setVisibility(View.VISIBLE);
                holder.mRightItemLayout.setData(storeGroup.rightStore);
            } else {
                holder.mRightItemLayout.setVisibility(View.INVISIBLE);
            }
        }

        if (position == 0) {
            holder.mTopLayout.setVisibility(View.GONE);
        } else {
            holder.mTopLayout.setVisibility(View.VISIBLE);
        }

        if (position == mStoreGroups.size() - 1) {
            holder.mBottomLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mBottomLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout mTopLayout;
        StoreItemLayout mLeftItemLayout;
        StoreItemLayout mMiddleItemLayout;
        StoreItemLayout mRightItemLayout;
        RelativeLayout mBottomLayout;
    }

}
