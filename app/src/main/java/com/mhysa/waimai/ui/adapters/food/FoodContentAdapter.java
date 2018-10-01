package com.mhysa.waimai.ui.adapters.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.food.FoodDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情
 * Date: 2017/8/8
 *
 * @author xusheng
 */

public class FoodContentAdapter extends BaseAdapter {

    private Context mContext;

    private List<FoodDetail> mFoodDetails = new ArrayList<FoodDetail>();

    public FoodContentAdapter(Context context, List<FoodDetail> foodDetails) {
        this.mContext = context;
        this.mFoodDetails = foodDetails;
    }

    @Override
    public int getCount() {
        if (null != mFoodDetails) {
            return mFoodDetails.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mFoodDetails && mFoodDetails.size() > position) {
            return mFoodDetails.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_food_content, null);

            holder.mContentSdv = (SimpleDraweeView) convertView.findViewById(R.id.sdv_content);
            holder.mContentTv = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FoodDetail foodDetail = mFoodDetails.get(position);
        if (null != foodDetail) {
            if (Utils.isChinese(mContext)) {
                StringUtils.getInstance().setText(foodDetail.content_cn, holder.mContentTv);
            } else {
                StringUtils.getInstance().setText(foodDetail.content_en, holder.mContentTv);
            }

            ImageUtils.getInstance().setImageURL(foodDetail.content_image, holder.mContentSdv);
        }

        return convertView;
    }

    class ViewHolder {
        TextView mContentTv;
        SimpleDraweeView mContentSdv;
    }

}
