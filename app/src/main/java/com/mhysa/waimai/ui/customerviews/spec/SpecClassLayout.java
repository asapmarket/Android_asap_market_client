package com.mhysa.waimai.ui.customerviews.spec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.gridview.CustomerGridView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.special.Special;
import com.mhysa.waimai.model.special.SpecialClass;
import com.mhysa.waimai.ui.adapters.spec.SpecListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/4
 *
 * @author xusheng
 */

public class SpecClassLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.tv_spec_class_name)
    TextView mClassNameTv;

    @Bind(R.id.cgv_spec)
    CustomerGridView mSpecGv;

    private List<Special> mSpecials = new ArrayList<Special>();

    private SpecListAdapter mAdapter;

    private ChangeSpecListener mListener;

    public SpecClassLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public SpecClassLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_spec_class, null);

        ButterKnife.bind(this, mRootView);

        mAdapter = new SpecListAdapter(mContext, mSpecials);
        mSpecGv.setAdapter(mAdapter);

        mSpecGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mSpecials && mSpecials.size() > position) {
                    if (mAdapter.getSelectedPosition() != position) {
                        mAdapter.setSelectedPosition(position);
                        if (null != mListener) {
                            mListener.changeSpec();
                        }
                    }
                }
            }
        });

        this.addView(mRootView);
    }

    public void setData(SpecialClass specialClass) {
        if (null != specialClass) {
            if (Utils.isChinese(mContext)) {
                StringUtils.getInstance().setText(specialClass.spec_class_name_cn, mClassNameTv);
            } else {
                StringUtils.getInstance().setText(specialClass.spec_class_name_en, mClassNameTv);
            }

            if (null != specialClass.spec_list
                    && specialClass.spec_list.size() > 0) {
                mSpecials.addAll(specialClass.spec_list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setListener(ChangeSpecListener listener) {
        this.mListener = listener;
    }

    public Special getSelectedSpecial() {
        Special special = null;

        try {
            special = mSpecials.get(mAdapter.getSelectedPosition());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return special;
    }

    public interface ChangeSpecListener {
        void changeSpec();
    }
}
