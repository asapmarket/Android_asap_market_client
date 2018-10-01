package com.mhysa.waimai.ui.customerviews.order;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
 * Date: 2017/8/14
 *
 * @author xusheng
 */

public class PaymethodChooseLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.rb_paypal)
    RadioButton mPaypalRb;

    @Bind(R.id.rb_card)
    RadioButton mCardRb;

    @Bind(R.id.rb_cash)
    RadioButton mCashRb;

    @Bind(R.id.rb_rewardpoint)
    RadioButton mRewardpointRb;

    @Bind(R.id.tv_convertible_rewardpoint)
    TextView mConvertibleRewardpointTv;

    private OnPaymethodChooseListener mListener;

    public PaymethodChooseLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public PaymethodChooseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_choose_paymethod, null);

        ButterKnife.bind(this, mRootView);

        LinearLayout contentLayout = (LinearLayout) mRootView.findViewById(R.id.layout_content);
        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(AppConfig.getScreenWidth() -
                        2 * ExtendUtils.getInstance().dip2px(mContext, 20),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLayout.setLayoutParams(contentParams);

        mPaypalRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCardRb.setChecked(false);
                    mCashRb.setChecked(false);
                    mRewardpointRb.setChecked(false);
                }
            }
        });

        mCardRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPaypalRb.setChecked(false);
                    mCashRb.setChecked(false);
                    mRewardpointRb.setChecked(false);
                }
            }
        });

        mRewardpointRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPaypalRb.setChecked(false);
                    mCardRb.setChecked(false);
                    mCashRb.setChecked(false);
                }
            }
        });

        mCashRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPaypalRb.setChecked(false);
                    mCardRb.setChecked(false);
                    mRewardpointRb.setChecked(false);
                }
            }
        });

        mCashRb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSelected(FusionCode.PayMethod.PAY_METHOD_CASH);
                }
            }
        });

        mPaypalRb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSelected(FusionCode.PayMethod.PAY_METHOD_PAYPAL);
                }
            }
        });

        mCardRb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSelected(FusionCode.PayMethod.PAY_METHOD_CREDIT_CARD);
                }
            }
        });

        mRewardpointRb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onSelected(FusionCode.PayMethod.PAY_REWARD_POINT);
                }
            }
        });

        this.addView(mRootView);
    }

    public void setListener(OnPaymethodChooseListener listener) {
        this.mListener = listener;
    }

    public void setIsErrand(boolean isErrand) {
        mCardRb.setVisibility(isErrand ? View.VISIBLE : View.VISIBLE);
    }

    public void setPaymethod(String paymethod, double rewardpoint) {
        String rewardPointDes = mContext.getString(R.string.convertible_rewardpoint, rewardpoint + "");
        mConvertibleRewardpointTv.setText(rewardPointDes);
        int index = rewardPointDes.indexOf(rewardpoint + "");
        int length = (rewardpoint + "").length();
        ExtendUtils.getInstance().setSpan(mConvertibleRewardpointTv, index, index + length, Color.parseColor("#2196f3"));

        if (StringUtils.getInstance().isNullOrEmpty(paymethod)) {
            return;
        }

        mPaypalRb.setChecked(false);
        mCardRb.setChecked(false);
        mCashRb.setChecked(false);
        mRewardpointRb.setChecked(false);

        if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_PAYPAL)) {
            mPaypalRb.setChecked(true);
        } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_CREDIT_CARD)) {
            mCardRb.setChecked(true);
        } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_CASH)) {
            mCashRb.setChecked(true);
        } else if (paymethod.equals(FusionCode.PayMethod.PAY_REWARD_POINT)) {
            mRewardpointRb.setChecked(true);
        }
    }

    public interface OnPaymethodChooseListener {
        void onSelected(String paymethod);
    }
}
