package com.mhysa.waimai.ui.fragments.wallet;

import android.app.Dialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.wallet.RewardPoint;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.utils.PromptDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by xusheng3 on 2017/12/25.
 */

public class WalletFragment extends BaseFragment {

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.tv_reward_point_account)
    TextView mRewardpointAccountTv;

    @Bind(R.id.tv_current_account)
    TextView mCurrentAccountTv;

    @Bind(R.id.tv_reward_point_des1)
    TextView mRewardPointDes1;

    @Bind(R.id.tv_reward_point_des2)
    TextView mRewardPointDes2;

    @Bind(R.id.tv_insufficient)
    TextView mInsufficientTv;

    @Bind(R.id.tv_exchange_ratio)
    TextView mExchangeRatioTv;

    private RewardPoint mRewardPoint;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_wallet;
    }

    @Override
    protected void initData() {
        super.initData();
        showNetDialog("");
        getData();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);

        getData();
    }

    private void getData() {
        OkHttpClientManager.getInstance().getRewardPoint(
                Utils.getUserId(mContext),
                Utils.getToken(mContext),
                new WtNetWorkListener<RewardPoint>() {
                    @Override
                    public void onSucess(RemoteReturnData<RewardPoint> data) {
                        if (null != data && null != data.data) {
                            mRewardPoint = data.data;
                            try {
                                showData(data.data);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(mContext, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                }
        );
    }

    private void showData(RewardPoint point) {
        mRewardpointAccountTv.setText(point.point + "");
        mCurrentAccountTv.setText(point.money + "");
        int radio = point.ratio;
        int min_exchange = 500;
        try {
            min_exchange = point.min_exchange;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String rewardPointDes2 = "";
        if (point.money < min_exchange * radio) {
            mInsufficientTv.setVisibility(View.VISIBLE);
        } else {
            mInsufficientTv.setVisibility(View.INVISIBLE);
        }

        rewardPointDes2 = getString(R.string.reward_point_des2, point.total_money + "");
        int index = rewardPointDes2.indexOf(point.total_money + "");
        int length = ("" + point.total_money).length();
        SpannableString styledText = new SpannableString(rewardPointDes2);
        styledText.setSpan(new TextAppearanceSpan(mContext, R.style.style_normal), 0, index - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(mContext, R.style.style_bold), index, index + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mRewardPointDes2.setText(styledText, TextView.BufferType.SPANNABLE);

        String rewardPointDes1 = getString(R.string.reward_point_des1, min_exchange * radio + " : " + min_exchange + "");
        int index1 = rewardPointDes1.indexOf(min_exchange * radio + " : " + min_exchange + "");
        int length1 = (min_exchange * radio + " : " + min_exchange + "").length();
        SpannableString styledText1 = new SpannableString(rewardPointDes1);
        styledText1.setSpan(new TextAppearanceSpan(mContext, R.style.style_normal), 0, index1 - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText1.setSpan(new TextAppearanceSpan(mContext, R.style.style_bold), index1, index1 + length1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mRewardPointDes1.setText(styledText1, TextView.BufferType.SPANNABLE);

        mExchangeRatioTv.setText(getString(R.string.exchange_ratio, min_exchange * radio + ""));
    }

    @OnClick({R.id.layout_reward_point, R.id.btn_exchange})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_reward_point: {
                // 什么是饭点
                showTipDialog();
                break;
            }

            case R.id.btn_exchange: {
                // 兑换
                exchange();
                break;
            }

            default: {
                break;
            }
        }
    }

    private void exchange() {
        showNetDialog("");
        OkHttpClientManager.getInstance().exchange(Utils.getUserId(mContext), Utils.getToken(mContext),
                new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        if (null != data) {
                            getData();
                            showSuccessDialog();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(mContext, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                });
    }

    private Dialog mSuccessDialog;

    private void showSuccessDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_exchange_success, null);
        view.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSuccessDialog.dismiss();
                mSuccessDialog = null;
            }
        });
        PromptDialogBuilder builder = new PromptDialogBuilder(mContext);
        builder.setIsHasBackground(false);
        builder.setView(view);
        mSuccessDialog = builder.create();
        mSuccessDialog.show();
    }

    private Dialog mTipDialog;

    private void showTipDialog() {
        if (null != mRewardPoint) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_rewardpoint_tip, null);
            view.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTipDialog.dismiss();
                    mTipDialog = null;
                }
            });

            TextView tipTv = (TextView) view.findViewById(R.id.tv_message);
            tipTv.setText(Utils.isChinese(mContext) ? mRewardPoint.info_cn : mRewardPoint.info_en);

            PromptDialogBuilder builder = new PromptDialogBuilder(mContext);
            builder.setIsHasBackground(false);
            builder.setView(view);
            mTipDialog = builder.create();
            mTipDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        if (null == event) {
            return;
        }

        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.PAY_SUCCESS: {
                getData();
                break;
            }
        }
    }
}
