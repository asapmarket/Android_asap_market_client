package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class OrderScheduleLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.iv_one)
    ImageView mOneIv;

    @Bind(R.id.iv_one_more)
    ImageView mOneMoreIv;

    @Bind(R.id.iv_two)
    ImageView mTwoIv;

    @Bind(R.id.iv_three)
    ImageView mThreeIv;

    @Bind(R.id.iv_four)
    ImageView mFourIv;

    @Bind(R.id.layout_one_right)
    LinearLayout mOneRightLayout;

    @Bind(R.id.layout_one_left_more)
    LinearLayout mOneLeftMoreLayout;

    @Bind(R.id.layout_one_right_more)
    LinearLayout mOneRightMoreLayout;

    @Bind(R.id.layout_two_left)
    LinearLayout mTwoLeftLayout;

    @Bind(R.id.layout_two_right)
    LinearLayout mTwoRightLayout;

    @Bind(R.id.layout_three_left)
    LinearLayout mThreeLeftLayout;

    @Bind(R.id.layout_three_right)
    LinearLayout mThreeRightLayout;

    @Bind(R.id.layout_four_left)
    LinearLayout mFourLeftLayout;

    @Bind(R.id.tv_taken)
    TextView mTakenTv;

    @Bind(R.id.tv_three)
    TextView mThreeTv;

    public OrderScheduleLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public OrderScheduleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_order_schedule, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setStatus(boolean isErrandOrder, String status, String name) {
        if (StringUtils.getInstance().isNullOrEmpty(status)) {
            return;
        }
        mOneIv.setImageResource(R.drawable.bg_corner_6dp_999999);
        mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_999999);
        mTwoIv.setImageResource(R.drawable.bg_corner_6dp_999999);
        mThreeIv.setImageResource(R.drawable.bg_corner_6dp_999999);
        mFourIv.setImageResource(R.drawable.bg_corner_6dp_999999);

        mOneRightLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mTwoLeftLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mTwoRightLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mThreeLeftLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mThreeRightLayout.setBackgroundColor(Color.parseColor("#cccccc"));
        mFourLeftLayout.setBackgroundColor(Color.parseColor("#cccccc"));

        if (isErrandOrder) {
            mThreeTv.setText(R.string.order_status_feedback);
        }

        switch (status) {
            case FusionCode.OrderStatus.ORDER_STATUS_CHECKOUT_SUCCESS: {
                // 下单成功
                mTakenTv.setText(mContext.getResources().getString(R.string.order_status_untaken));
                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_TAKEN: {
                // 接单
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTakenTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));

                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_IN_PIECE:
            case FusionCode.OrderStatus.ORDER_STATUS_FEEDBACK: {
                // 取件中或者反馈
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTakenTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_ON_ROAD: {
                // 在路上
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTakenTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mTwoRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mThreeLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mThreeIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_FINISHED: {
                // 订单完成
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTakenTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mTwoRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mThreeLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mThreeIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mThreeRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mFourLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mFourIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }
        }

        int screenWidth = AppConfig.getScreenWidth();

        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTakenTv.measure(spec, spec);
        int width = mTakenTv.getMeasuredWidth();

        int lineWidth = (screenWidth / 5 - ExtendUtils.getInstance().dip2px(mContext, 12)) / 2;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (width / 2 > lineWidth * 3 + ExtendUtils.getInstance().dip2px(mContext, 18)) {
            params.leftMargin = ExtendUtils.getInstance().dip2px(mContext, 5);
            params.rightMargin = ExtendUtils.getInstance().dip2px(mContext, 5);
        } else {
            params.leftMargin = lineWidth * 3 + ExtendUtils.getInstance().dip2px(mContext, 18) - width / 2;
            params.rightMargin = ExtendUtils.getInstance().dip2px(mContext, 5);
        }

        mTakenTv.setLayoutParams(params);
    }
}
