package com.mhysa.waimai.ui.activities.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.mhysa.waimai.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/10/12
 *
 * @author xusheng
 */

public class ContactUsActivity extends BaseActivity {

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @OnClick({R.id.layout_back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                ContactUsActivity.this.finish();
                break;
            }

            default: {
                break;
            }
        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, ContactUsActivity.class);
        context.startActivity(intent);
    }
}
