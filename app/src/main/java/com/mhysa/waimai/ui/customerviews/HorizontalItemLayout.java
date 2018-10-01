package com.mhysa.waimai.ui.customerviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mhysa.waimai.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class HorizontalItemLayout extends RelativeLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.iv_icon)
    ImageView mIconIv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.view_divider)
    View mDividerView;

    private boolean mshowDivider;

    private int mIconId;

    private int mTextId;

    public HorizontalItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public HorizontalItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Mhysa_HorizontalItem);
        mshowDivider = typedArray.getBoolean(R.styleable.Mhysa_HorizontalItem_showDivider, false);
        mTextId = typedArray.getResourceId(R.styleable.Mhysa_HorizontalItem_text, 0);
        mIconId = typedArray.getResourceId(R.styleable.Mhysa_HorizontalItem_item_icon, 0);

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_horizontal_item, null);

        ButterKnife.bind(this, mRootView);

        mDividerView.setVisibility(mshowDivider ? View.VISIBLE : View.GONE);
        if (mIconId > 0) {
            mIconIv.setVisibility(View.VISIBLE);
            mIconIv.setImageResource(mIconId);
        } else {
            mIconIv.setVisibility(View.GONE);
        }

        if (mTextId > 0) {
            mNameTv.setHint(mTextId);
        }

        this.addView(mRootView);
    }
}
