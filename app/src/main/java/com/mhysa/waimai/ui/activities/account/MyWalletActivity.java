package com.mhysa.waimai.ui.activities.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.mhysa.waimai.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的钱包
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class MyWalletActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_my_wallet;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.my_wallet);
    }

    @OnClick({R.id.layout_back, R.id.btn_recharge, R.id.btn_withdraw})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                MyWalletActivity.this.finish();
                break;
            }

            case R.id.btn_recharge: {
                RechargeActivity.invoke(MyWalletActivity.this);
                break;
            }

            case R.id.btn_withdraw: {
                WithdrawActivity.invoke(MyWalletActivity.this);
                break;
            }

            default: {
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, MyWalletActivity.class);
        context.startActivity(intent);
    }
}
