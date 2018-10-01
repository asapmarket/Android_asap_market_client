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
import android.widget.EditText;

import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;

/**
 * 输入优惠码
 * Date: 2018/8/21
 *
 * @author xusheng3
 */

public class InputCouponCodeFragment extends DialogFragment {

    private OnFeedbackListener mListener;

    private EditText mCouponEt;

    public void setListener(OnFeedbackListener listener) {
        this.mListener = listener;
    }

    public InputCouponCodeFragment() {
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

        View view = inflater.inflate(R.layout.fragment_input_coupon_code, container, false);

        mCouponEt = (EditText) view.findViewById(R.id.et_coupon_code);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputCouponCodeFragment.this.dismiss();
            }
        });

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = mCouponEt.getText().toString().trim();
                if (StringUtils.getInstance().isNullOrEmpty(result)) {
                    PromptUtils.getInstance().showShortPromptToast(getActivity(), R.string.input_coupon_code);
                } else {
                    if (null != mListener) {
                        mListener.onConfirm(result);
                        InputCouponCodeFragment.this.dismiss();
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


    public interface OnFeedbackListener {

        void onConfirm(String result);

    }
}
