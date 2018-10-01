package com.mhysa.waimai.ui.adapters.message;

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
import com.mhysa.waimai.model.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员消息
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class MessageListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Message> mMessages = new ArrayList<Message>();

    public MessageListAdapter(Context context, List<Message> messages) {
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {

        if (null != mMessages) {
            return mMessages.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mMessages && mMessages.size() > position) {
            return mMessages.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_message, null);

            holder.mIconSdv = (SimpleDraweeView) convertView.findViewById(R.id.sdv_icon);
            holder.mTitleTv = (TextView) convertView.findViewById(R.id.tv_title);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);
            holder.mContentTv = (TextView) convertView.findViewById(R.id.tv_content);
            holder.mDividerView = (View) convertView.findViewById(R.id.view_divider);
            holder.mDividerView1 = (View) convertView.findViewById(R.id.view_divider1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Message message = mMessages.get(position);
        if (null != message) {
            ImageUtils.getInstance().setImageURL(message.image, holder.mIconSdv);

            if (Utils.isChinese(mContext)) {
                holder.mTitleTv.setText(StringUtils.getInstance().isNullOrEmpty(message.title_cn)
                        ? "" : message.title_cn);

                holder.mContentTv.setText(StringUtils.getInstance().isNullOrEmpty(message.desc_cn)
                        ? "" : message.desc_cn);
            } else {
                holder.mTitleTv.setText(StringUtils.getInstance().isNullOrEmpty(message.title_en)
                        ? "" : message.title_en);
                holder.mContentTv.setText(StringUtils.getInstance().isNullOrEmpty(message.desc_en)
                        ? "" : message.desc_en);
            }

            holder.mTimeTv.setText(StringUtils.getInstance().isNullOrEmpty(message.create_time)
                    ? "" : message.create_time);
        }

        if (position == getCount() - 1) {
            holder.mDividerView.setVisibility(View.GONE);
            holder.mDividerView1.setVisibility(View.GONE);
        } else {
            holder.mDividerView.setVisibility(View.VISIBLE);
            holder.mDividerView1.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mIconSdv;
        TextView mTitleTv;
        TextView mTimeTv;
        TextView mContentTv;
        View mDividerView;
        View mDividerView1;
    }
}
