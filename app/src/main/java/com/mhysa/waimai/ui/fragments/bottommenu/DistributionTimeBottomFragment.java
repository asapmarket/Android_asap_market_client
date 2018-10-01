package com.mhysa.waimai.ui.fragments.bottommenu;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mhysa.waimai.R;
import com.mhysa.waimai.model.order.DistributionTime;
import com.mhysa.waimai.ui.adapters.order.DistributionTimeListAdapter;

import java.util.List;

/**
 * 文件描述
 * Date: 2018/3/10
 *
 * @author xusheng
 */

public class DistributionTimeBottomFragment extends DialogFragment {

    private OnMenuSelectedListener mListener;

    private ListView mListView;

    private List<DistributionTime> mDistributionTimes;

    public void setListener(OnMenuSelectedListener listener) {
        this.mListener = listener;
    }

    public DistributionTimeBottomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明

        getDialog().getWindow().setWindowAnimations(R.style.menu_animation);//添加一组进出动画

        View view = inflater.inflate(R.layout.fragment_distributiontime, container, false);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistributionTimeBottomFragment.this.dismiss();
            }
        });

        mListView = (ListView) view.findViewById(R.id.lv_distributiontime);

        DistributionTimeListAdapter adapter = new DistributionTimeListAdapter(getActivity(), mDistributionTimes);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mDistributionTimes && mDistributionTimes.size() > position) {
                    if (null != mListener) {
                        mListener.onMenuSelected(mDistributionTimes.get(position));
                        DistributionTimeBottomFragment.this.dismiss();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);
    }

    @Override
    public void onStop() {
        this.getView().setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_disappear));
        super.onStop();
    }

    public void setData(final List<DistributionTime> datas) {
        mDistributionTimes = datas;
    }

    public interface OnMenuSelectedListener {
        void onMenuSelected(DistributionTime time);
    }
}
