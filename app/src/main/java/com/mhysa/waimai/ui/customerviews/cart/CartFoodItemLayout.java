package com.mhysa.waimai.ui.customerviews.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.model.cart.CartFood;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/5
 *
 * @author xusheng
 */

public class CartFoodItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.iv_check)
    ImageView mCheckIv;

    @Bind(R.id.sdv_food_image)
    SimpleDraweeView mFoodImageSdv;

    @Bind(R.id.tv_quality)
    TextView mQualityTv;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.tv_food_name)
    TextView mFoodNameTv;

    @Bind(R.id.tv_special)
    TextView mSpecialTv;

    @Bind(R.id.iv_add)
    ImageView mAddIv;

    @Bind(R.id.iv_minus)
    ImageView mMinusIv;

    private boolean mIsSelected = false;

    private CartFoodItemListener mListener;

    private CartFood mCartFood;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CartManager.DELETE_ALL_TARGET_FOOD_SUCCESS: {
                    if (null != mListener) {
                        mListener.OnDelete();
                    }
                    break;
                }
            }
        }
    };

    public CartFoodItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public CartFoodItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_cart_food_item, null);

        ButterKnife.bind(this, mRootView);

        this.addView(mRootView);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedStatus();

                if (null != mListener) {
                    mListener.OnStatusChanged();
                }
            }
        });

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 删除该条记录
                AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(mContext, true);
                builder.setMessage(R.string.delete_food_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                CartManager.getInstance().deleteAllTargetFood(mHandler,
                                        mCartFood.foods_id, mCartFood.store_id, mCartFood.spec_id_list);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });

        mCheckIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedStatus();

                if (null != mListener) {
                    mListener.OnStatusChanged();
                }
            }
        });

        mAddIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartFood.quality = mCartFood.quality + 1;
                if (null != mListener) {
                    mListener.onNumChanged();
                }

                mQualityTv.setText(mCartFood.quality + "");

                if (mCartFood.quality > 1) {
                    mMinusIv.setImageResource(R.mipmap.ic_minus_pressed);
                } else {
                    mMinusIv.setImageResource(R.mipmap.ic_minus_normal);
                }

                CartManager.getInstance().addFood(mCartFood, mHandler);
            }
        });

        mMinusIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCartFood.quality > 1) {
                    mCartFood.quality = mCartFood.quality - 1;
                    if (null != mListener) {
                        mListener.onNumChanged();
                    }

                    mQualityTv.setText(mCartFood.quality + "");

                    if (mCartFood.quality > 1) {
                        mMinusIv.setImageResource(R.mipmap.ic_minus_pressed);
                    } else {
                        mMinusIv.setImageResource(R.mipmap.ic_minus_normal);
                    }

                    CartManager.getInstance().deleteFood(mHandler, mCartFood.foods_id, mCartFood.store_id, mCartFood.spec_id_list);
                }
            }
        });
    }

    public void setListener(CartFoodItemListener listener) {
        this.mListener = listener;
    }

    public void setData(CartFood food) {
        if (null == food) {
            return;
        }

        mCartFood = food;

        if (Utils.isChinese(mContext)) {
            ImageUtils.getInstance().setImageURL(food.foods_image_cn, mFoodImageSdv);
            StringUtils.getInstance().setText(food.foods_name_cn, mFoodNameTv);
            StringUtils.getInstance().setText(food.specials_name_cn, mSpecialTv);
        } else {
            ImageUtils.getInstance().setImageURL(food.foods_image_en, mFoodImageSdv);
            StringUtils.getInstance().setText(food.foods_name_en, mFoodNameTv);
            StringUtils.getInstance().setText(food.specials_name_en, mSpecialTv);
        }

        mQualityTv.setText(food.quality + "");
        StringUtils.getInstance().setText("$" + food.price, mPriceTv);

        if (food.quality > 1) {
            mMinusIv.setImageResource(R.mipmap.ic_minus_pressed);
        } else {
            mMinusIv.setImageResource(R.mipmap.ic_minus_normal);
        }
    }

    /**
     * 修改选中状态
     */
    private void changeSelectedStatus() {
        mIsSelected = !mIsSelected;
        mCheckIv.setImageResource(mIsSelected ?
                R.mipmap.ic_rect_check_checked : R.mipmap.ic_rect_check_normal);
    }

    public void changeSelectedStatus(boolean isSelected) {
        mIsSelected = isSelected;
        mCheckIv.setImageResource(mIsSelected ?
                R.mipmap.ic_rect_check_checked : R.mipmap.ic_rect_check_normal);
    }

    public boolean getSelectedStatus() {
        return mIsSelected;
    }

    public BigDecimal getTotalPrice() {
        if (!mIsSelected || StringUtils.getInstance().isNullOrEmpty(mCartFood.price)) {
            return null;
        }

        BigDecimal resultPrice = new BigDecimal(0.0);
        BigDecimal price = BigDecimal.valueOf(Double.valueOf(mCartFood.price));
        for (int i = 0; i < mCartFood.quality; i++) {
            resultPrice = resultPrice.add(price);
        }

        return resultPrice;
    }

    public CartFood getSelectdFood() {
        if (mIsSelected) {
            return mCartFood;
        }

        return null;
    }

    public interface CartFoodItemListener {

        void OnStatusChanged();

        void OnDelete();

        void onNumChanged();
    }
}
