package com.mhysa.waimai.ui.customerviews.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mhysa.waimai.R;
import com.mhysa.waimai.model.home.Type;
import com.mhysa.waimai.ui.widget.HomeTypeViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2018/8/20
 *
 * @author xusheng3
 */

public class HomeTypeLayoutV2 extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_open_or_close)
    TextView mOpenOrCloseTv;

    @Bind(R.id.layout_type)
    HomeTypeViewGroup mTypeLayout;

    public OnTypeSelectedListener mListener;

    public HomeTypeLayoutV2(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public HomeTypeLayoutV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_home_type_v2, null);

        ButterKnife.bind(this, mRootView);
        mOpenOrCloseTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTypeLayout.getVisibility() == View.VISIBLE) {
                    mTypeLayout.setVisibility(View.GONE);
                    mOpenOrCloseTv.setText(R.string.open);
                    Drawable closeDown = getResources().getDrawable(R.mipmap.ic_home_type_arrow_down);
                    closeDown.setBounds(0, 0, closeDown.getMinimumWidth(), closeDown.getMinimumHeight());
                    mOpenOrCloseTv.setCompoundDrawables(null, null, closeDown, null);
                } else {
                    mTypeLayout.setVisibility(View.VISIBLE);
                    mOpenOrCloseTv.setText(R.string.close);
                    Drawable openUp = getResources().getDrawable(R.mipmap.ic_home_type_arrow_up);
                    openUp.setBounds(0, 0, openUp.getMinimumWidth(), openUp.getMinimumHeight());
                    mOpenOrCloseTv.setCompoundDrawables(null, null, openUp, null);
                }
            }
        });

        this.addView(mRootView);
    }

    public void setData(final List<Type> types) {
        if (null == types || types.size() == 0) {
            return;
        }

        mTypeLayout.addItemViews(types, HomeTypeViewGroup.BTN_MODE);
        mTypeLayout.setItemTextSize(12);
        mTypeLayout.chooseItemStyle(0);
        mTypeLayout.setGroupClickListener(new HomeTypeViewGroup.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(int item) {
                if (null != mListener && types.size() > item && null != types.get(item)) {
                    mListener.onTypeSelectd(types.get(item));
                }
            }
        });
    }

    public void setListener(OnTypeSelectedListener listener) {
        this.mListener = listener;
    }

    public interface OnTypeSelectedListener {
        void onTypeSelectd(Type type);
    }
}
