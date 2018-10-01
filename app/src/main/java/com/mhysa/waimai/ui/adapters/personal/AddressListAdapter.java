package com.mhysa.waimai.ui.adapters.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.ui.activities.personal.EditAddressActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 收货地址
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class AddressListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Address> mAddresses = new ArrayList<Address>();

    private boolean mIsChoose = false;

    private AddressListListener mListener;

    public AddressListAdapter(Context context, List<Address> addresses, boolean isChoose) {
        this.mContext = context;
        this.mAddresses = addresses;
        this.mIsChoose = isChoose;
    }

    @Override
    public int getCount() {

        if (null != mAddresses) {
            return mAddresses.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mAddresses && mAddresses.size() > position) {
            return mAddresses.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_address, null);

            holder.mCheckIv = (ImageView) convertView.findViewById(R.id.iv_check);
            holder.mAddressTv = (TextView) convertView.findViewById(R.id.tv_address);
            holder.mEditTv = (TextView) convertView.findViewById(R.id.tv_edit);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mSexTv = (TextView) convertView.findViewById(R.id.tv_sex);
            holder.mPhoneTv = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Address address = mAddresses.get(position);
        if (null != address) {
            holder.mAddressTv.setText(StringUtils.getInstance().isNullOrEmpty(address.address) ? "" : address.address);
            holder.mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(address.extm_name) ? "" : address.extm_name);
            holder.mPhoneTv.setText(StringUtils.getInstance().isNullOrEmpty(address.extm_phone) ? "" : address.extm_phone);

            if (!StringUtils.getInstance().isNullOrEmpty(address.sex)
                    && address.sex.equals(FusionCode.Sex.FEMALE)) {
                holder.mSexTv.setText(R.string.female);
            } else {
                holder.mSexTv.setText(R.string.male);
            }

            if (!StringUtils.getInstance().isNullOrEmpty(address.is_default)
                    && address.is_default.equals(FusionCode.DefaultAddress.IS_DEFAULT)) {
                holder.mCheckIv.setImageResource(R.mipmap.ic_rect_check_checked);
            } else {
                holder.mCheckIv.setImageResource(R.mipmap.ic_rect_check_normal);
            }

            holder.mEditTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditAddressActivity.invoke(mContext, address, position);
                }
            });

            holder.mCheckIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsChoose && null != mListener) {
                        mListener.onChoose(address);
                    }

                    if (!mIsChoose && null != mListener) {
                        if (StringUtils.getInstance().isNullOrEmpty(address.is_default)
                                || address.is_default.equals(FusionCode.DefaultAddress.IS_NOT_DEFAULT)) {
                            // 设置默认地址
                            mListener.onSetDefault(address, position);
                        }
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsChoose && null != mListener) {
                        mListener.onChoose(address);
                    }

                    if (!mIsChoose && null != mListener) {
                        // 设置默认地址
                        if (StringUtils.getInstance().isNullOrEmpty(address.is_default)
                                || address.is_default.equals(FusionCode.DefaultAddress.IS_NOT_DEFAULT)) {
                            // 设置默认地址
                            mListener.onSetDefault(address, position);
                        }
                    }
                }
            });
        }

        return convertView;
    }

    public void setListener(AddressListListener listener) {
        this.mListener = listener;
    }

    class ViewHolder {
        ImageView mCheckIv;
        TextView mAddressTv;
        TextView mNameTv;
        TextView mSexTv;
        TextView mPhoneTv;
        TextView mEditTv;
    }

    public interface AddressListListener {
        void onChoose(Address address);

        void onSetDefault(Address address, int position);
    }
}
