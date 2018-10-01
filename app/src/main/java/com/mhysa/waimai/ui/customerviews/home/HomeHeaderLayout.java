package com.mhysa.waimai.ui.customerviews.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.home.HomeInfo;
import com.mhysa.waimai.model.home.Type;
import com.mhysa.waimai.ui.activities.errand.ErrandHomeActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.customerviews.scrollloop.AutoScrollPlayView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页header
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class HomeHeaderLayout extends LinearLayout {

    public static final float CAROUSEL_RATIO = 420 / 750f;

    private Context mContext;

    private View mRootView;

    @Bind(R.id.layout_auto_play)
    AutoScrollPlayView mAutoScrollPlayView;

    @Bind(R.id.layout_type)
    HomeTypeLayoutV2 mTypeLayout;

    @Bind(R.id.layout_errand)
    LinearLayout mErrandLayout;

    public HomeTypeLayoutV2.OnTypeSelectedListener mListener;

    public HomeHeaderLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public HomeHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_home_header, null);

        ButterKnife.bind(this, mRootView);

        //图片高度设置
        mAutoScrollPlayView.getLayoutParams().height = (int) (AppConfig.getScreenWidth() * CAROUSEL_RATIO);
        this.addView(mRootView);

        mErrandLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.getInstance().isNullOrEmpty(Utils.getToken(mContext))) {
                    LoginActivity.invoke(mContext, false);
                } else {
                    ErrandHomeActivity.forward(mContext);
                }
            }
        });
    }

    public void setData(HomeInfo homeInfo) {
        if (null == homeInfo) {
            return;
        }

        if (null != homeInfo.banner_list
                && homeInfo.banner_list.size() > 0) {
            mAutoScrollPlayView.setVisibility(View.VISIBLE);
            mAutoScrollPlayView.bindAutoScrollPlayViewData(homeInfo.banner_list, CAROUSEL_RATIO);
        } else {
            mAutoScrollPlayView.setVisibility(View.GONE);
        }

        if (null != homeInfo.type_list
                && homeInfo.type_list.size() > 0) {
            mTypeLayout.setVisibility(View.VISIBLE);
            mTypeLayout.setData(homeInfo.type_list);
            mTypeLayout.setListener(new HomeTypeLayoutV2.OnTypeSelectedListener() {
                @Override
                public void onTypeSelectd(Type type) {
                    if (null != mListener) {
                        mListener.onTypeSelectd(type);
                    }
                }
            });

        } else {
            mTypeLayout.setVisibility(View.GONE);
        }
    }

    public void setListener(HomeTypeLayoutV2.OnTypeSelectedListener listener) {
        this.mListener = listener;
    }
}
