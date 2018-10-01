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

/**
 * 提现
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class WithdrawActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.withdraw);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, WithdrawActivity.class);
        context.startActivity(intent);
    }
}
