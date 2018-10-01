package com.mhysa.waimai.ui.customerviews.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.activities.store.StoreDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商家列表item
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class StoreItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.sdv_icon)
    SimpleDraweeView mIconSdv;

    @Bind(R.id.tv_work_off)
    TextView mWorkOffTv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_description)
    TextView mDescriptionTv;

    private int mImageWidth;

    private int mImageHeight;

    private Store mStore;

    public StoreItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public StoreItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_store_item, null);

        ButterKnife.bind(this, mRootView);

        mImageWidth = (AppConfig.getScreenWidth() -
                4 * ExtendUtils.getInstance().dip2px(mContext, 10)) / 3;
        mImageHeight = mImageWidth;

        LayoutParams layoutParams = new LayoutParams(mImageWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(mImageWidth,
                mImageHeight);

        mIconSdv.setLayoutParams(imageParams);
        mWorkOffTv.setLayoutParams(imageParams);

        this.addView(mRootView, layoutParams);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mStore) {
                    StoreDetailActivity.invoke(mContext, mStore);
                }
            }
        });
    }

    public void setData(Store store) {
        if (null == store) {
            return;
        }

        mStore = store;

        if (StringUtils.getInstance().isNullOrEmpty(store.store_image)) {
            ImageUtils.getInstance().setImageURL("", mIconSdv);
        } else {
            ImageUtils.getInstance().setImageURL(store.store_image, mIconSdv);
        }

        if (Utils.isChinese(mContext)) {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_cn)
                    ? "" : store.store_name_cn);
            mDescriptionTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_desc_cn)
                    ? "" : store.store_desc_cn);
        } else {
            mNameTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_name_en)
                    ? "" : store.store_name_en);
            mDescriptionTv.setText(StringUtils.getInstance().isNullOrEmpty(store.store_desc_en)
                    ? "" : store.store_desc_en);
        }

        if (!StringUtils.getInstance().isNullOrEmpty(store.onwork) &&
                store.onwork.equals(FusionCode.WorkStatus.WORK_STATUS_OFFWORK)) {
            mWorkOffTv.setVisibility(View.VISIBLE);
        } else {
            mWorkOffTv.setVisibility(View.GONE);
        }

    }
}
