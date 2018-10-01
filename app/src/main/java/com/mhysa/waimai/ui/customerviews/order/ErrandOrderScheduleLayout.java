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
import com.mhysa.waimai.model.errand.ErrandOrder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2018/4/18
 *
 * @author xusheng
 */

public class ErrandOrderScheduleLayout extends LinearLayout {

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

    @Bind(R.id.tv_one)
    TextView mOneTv;

    @Bind(R.id.tv_two)
    TextView mTwoTv;

    @Bind(R.id.tv_three)
    TextView mThreeTv;

    @Bind(R.id.tv_four)
    TextView mFourTv;

    @Bind(R.id.tv_five)
    TextView mFiveTv;

    public ErrandOrderScheduleLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public ErrandOrderScheduleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_errand_order_schedule, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
    }

    public void setStatus(ErrandOrder errandOrder) {
        if (null == errandOrder || StringUtils.getInstance().isNullOrEmpty(errandOrder.state)) {
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

        final String status = errandOrder.state;
        final String name = errandOrder.exp_name;
        switch (status) {
            case FusionCode.OrderStatus.ORDER_STATUS_CHECKOUT_SUCCESS: {
                // 下单成功
                mTwoTv.setText(mContext.getResources().getString(R.string.order_status_wait_for_taken));
                mThreeTv.setText(mContext.getResources().getString(R.string.order_status_wait_for_feedback));
                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_TAKEN: {
                // 已经接单
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTwoTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }
                mThreeTv.setText(mContext.getResources().getString(R.string.order_status_wait_for_feedback));

                mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mTwoLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_FEEDBACK: {
                // 已经反馈，区分是否付款
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTwoTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                if (!StringUtils.getInstance().isNullOrEmpty(errandOrder.pay_state)
                        && errandOrder.pay_state.equals(FusionCode.PayStatus.PAY_STATUS_PAIED)) {
                    // 已经支付
                    if (errandOrder.payment_method.equals(FusionCode.PayMethod.PAY_METHOD_CASH)) {
                        // 现金支付
                        mThreeTv.setText(mContext.getResources().getString(R.string.order_status_pay_in_cash));
                    } else {
                        mThreeTv.setText(mContext.getResources().getString(R.string.order_status_paid));
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
                } else {
                    // 已经反馈价格，没有支付
                    mThreeTv.setText(mContext.getResources().getString(R.string.order_status_wait_for_pay));
                    mOneIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                    mOneRightLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                    mOneLeftMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                    mOneMoreIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                    mOneRightMoreLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                    mTwoLeftLayout.setBackgroundColor(Color.parseColor("#2196f3"));
                    mTwoIv.setImageResource(R.drawable.bg_corner_6dp_2196f3);
                }

                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_ON_ROAD: {
                // 在路上
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTwoTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                if (errandOrder.payment_method.equals(FusionCode.PayMethod.PAY_METHOD_CASH)) {
                    // 现金支付
                    mThreeTv.setText(mContext.getResources().getString(R.string.order_status_pay_in_cash));
                } else {
                    mThreeTv.setText(mContext.getResources().getString(R.string.order_status_paid));
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
                break;
            }

            case FusionCode.OrderStatus.ORDER_STATUS_FINISHED: {
                // 订单完成
                if (!StringUtils.getInstance().isNullOrEmpty(name)) {
                    mTwoTv.setText(mContext.getResources().getString(R.string.order_status_taken, name));
                }

                if (errandOrder.payment_method.equals(FusionCode.PayMethod.PAY_METHOD_CASH)) {
                    // 现金支付
                    mThreeTv.setText(mContext.getResources().getString(R.string.order_status_pay_in_cash));
                } else {
                    mThreeTv.setText(mContext.getResources().getString(R.string.order_status_paid));
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
        mTwoTv.measure(spec, spec);
        int width = mTwoTv.getMeasuredWidth();

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

        mTwoTv.setLayoutParams(params);
    }
}
