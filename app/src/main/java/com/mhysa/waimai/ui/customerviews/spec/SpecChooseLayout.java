package com.mhysa.waimai.ui.customerviews.spec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.special.Price;
import com.mhysa.waimai.model.special.Special;
import com.mhysa.waimai.model.special.SpecialClass;
import com.mhysa.waimai.model.store.Store;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/5
 *
 * @author xusheng
 */

public class SpecChooseLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.layout_spec_class)
    LinearLayout mSpecClassLayout;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.tv_number)
    TextView mNumberTv;

    @Bind(R.id.iv_add)
    ImageView mAddIv;

    @Bind(R.id.iv_minus)
    ImageView mMinusIv;

    private OnConfirmListener mListener;

    private String ids;

    private String specials_name_cn;

    private String specials_name_en;

    private int mNumber = 1;

    // 一份价格
    private String mPrice;

    private Food mFood;

    public SpecChooseLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public SpecChooseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_choose_spec, null);

        ButterKnife.bind(this, mRootView);

        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCancel();
                }
            }
        });

        mAddIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumber = mNumber + 1;
                mMinusIv.setVisibility(View.VISIBLE);
                mNumberTv.setText("" + mNumber);
                mNumberTv.setVisibility(View.VISIBLE);
            }
        });

        mMinusIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumber = mNumber - 1;
                if (mNumber > 0) {
                    mNumberTv.setText("" + mNumber);
                } else {
                    mMinusIv.setVisibility(View.GONE);
                    mNumberTv.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout contentLayout = (LinearLayout) mRootView.findViewById(R.id.layout_content);
        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(AppConfig.getScreenWidth() -
                        2 * ExtendUtils.getInstance().dip2px(mContext, 20),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLayout.setLayoutParams(contentParams);

        mNumberTv.setText(mNumber + "");

        this.addView(mRootView);
    }

    public void setData(Food food, Store store, List<SpecialClass> spec_class_list) {
        if (null == food
                || null == store
                || null == spec_class_list
                || spec_class_list.size() == 0) {
            return;
        }

        mFood = food;
        StringUtils.getInstance().setText("$" + food.price, mPriceTv);
        mPrice = food.price;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ExtendUtils.getInstance().dip2px(mContext, 10);

        mSpecClassLayout.removeAllViews();
        final int size = spec_class_list.size();
        for (int i = 0; i < size; i++) {
            SpecClassLayout specClassLayout = new SpecClassLayout(mContext);
            specClassLayout.setData(spec_class_list.get(i));
            specClassLayout.setListener(new SpecClassLayout.ChangeSpecListener() {
                @Override
                public void changeSpec() {
                    reBuilder();
                    getPrice();
                }
            });
            mSpecClassLayout.addView(specClassLayout, params);
        }

        reBuilder();
    }

    public void setListener(OnConfirmListener listener) {
        this.mListener = listener;
    }

    private void confirm() {
        if (null != mListener) {
            mListener.onConfirm(ids, specials_name_cn, specials_name_en, mNumber, mPrice);
        }
    }

    private void getPrice() {
        OkHttpClientManager.getInstance().getSpecialPrice(Utils.getUserId(mContext),
                Utils.getToken(mContext), mFood.foods_id, ids, new WtNetWorkListener<Price>() {
                    @Override
                    public void onSucess(RemoteReturnData<Price> data) {
                        if (null != data && null != data.data) {
                            mPrice = data.data.price;
                            mPriceTv.setText("$" + mPrice);
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {

                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    private void reBuilder() {
        if (null != mSpecClassLayout && mSpecClassLayout.getChildCount() > 0) {
            final int count = mSpecClassLayout.getChildCount();
            StringBuilder idsBuilder = new StringBuilder();
            StringBuilder nameCnBuilder = new StringBuilder();
            StringBuilder nameEnBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                try {
                    Special special = ((SpecClassLayout) mSpecClassLayout.getChildAt(i)).getSelectedSpecial();
                    idsBuilder.append(special.spec_id);
                    nameCnBuilder.append(special.spec_name_cn);
                    nameEnBuilder.append(special.spec_name_en);

                    if (i != count - 1) {
                        idsBuilder.append(",");
                        nameCnBuilder.append(",");
                        nameEnBuilder.append(",");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }

            ids = idsBuilder.toString();
            specials_name_cn = nameCnBuilder.toString();
            specials_name_en = nameEnBuilder.toString();

            getPrice();
        }
    }

    public interface OnConfirmListener {

        void onConfirm(String ids, String specials_name_cn, String specials_name_en, int number, String price);

        void onCancel();
    }
}
