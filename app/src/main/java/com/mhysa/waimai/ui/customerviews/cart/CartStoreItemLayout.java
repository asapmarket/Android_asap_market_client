package com.mhysa.waimai.ui.customerviews.cart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.cart.CartFood;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文件描述
 * Date: 2017/8/5
 *
 * @author xusheng
 */

public class CartStoreItemLayout extends LinearLayout {

    private Context mContext;

    private View mRootView;

    @Bind(R.id.layout_store_name)
    RelativeLayout mStoreNameLayout;

    @Bind(R.id.iv_check)
    ImageView mCheckIv;

    @Bind(R.id.tv_store_name)
    TextView mStoreNameTv;

    @Bind(R.id.layout_foods)
    LinearLayout mFoodsLayout;

    private HashMap<String, List<CartFood>> mFoodMap = new HashMap<String, List<CartFood>>();

    private CartStoreItemListener mListener;

    private boolean mIsSelected = false;

    private String mStoreId;

    public CartStoreItemLayout(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public CartStoreItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.layout_cart_store_item, null);

        ButterKnife.bind(this, mRootView);

        mCheckIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedStatus();

                if (null != mListener) {
                    mListener.OnStatusChanged();
                }
            }
        });

        this.addView(mRootView);
    }

    /**
     * 修改选中状态
     */
    public void changeSelectedStatus() {
        mIsSelected = !mIsSelected;
        mCheckIv.setImageResource(mIsSelected ?
                R.mipmap.ic_rect_check_checked : R.mipmap.ic_rect_check_normal);

        if (null != mFoodsLayout && mFoodsLayout.getChildCount() > 0) {
            final int count = mFoodsLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                ((CartFoodItemLayout) mFoodsLayout.getChildAt(i)).changeSelectedStatus(mIsSelected);
            }

        }
    }

    public void setListener(CartStoreItemListener listener) {
        this.mListener = listener;
    }

    public void setData(String storeId, final List<CartFood> foods) {
        if (null == foods || foods.size() == 0) {
            return;
        }

        mStoreId = storeId;
        mStoreNameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != foods.get(0) && null != foods.get(0).store_id) {
//                    Store store = new Store();
//                    store.store_id = foods.get(0).store_id;
//                    StoreDetailActivity.invoke(mContext, store);
//                }

                changeSelectedStatus();

                if (null != mListener) {
                    mListener.OnStatusChanged();
                }
            }
        });

        if (Utils.isChinese(mContext)) {
            StringUtils.getInstance().setText(foods.get(0).store_name_cn,
                    mStoreNameTv);
        } else {
            StringUtils.getInstance().setText(foods.get(0).store_name_en,
                    mStoreNameTv);
        }

        mFoodMap.clear();
        final int size = foods.size();
        for (int i = 0; i < size; i++) {
            CartFood food = foods.get(i);

            String key = "";
            if (StringUtils.getInstance().isNullOrEmpty(food.spec_id_list)) {
                key = food.store_id + food.foods_id;
            } else {
                key = food.store_id + food.foods_id + food.spec_id_list;
            }

            if (mFoodMap.containsKey(key)) {
                List<CartFood> tempFood = mFoodMap.get(key);
                tempFood.add(food);
                mFoodMap.put(key, tempFood);
            } else {
                List<CartFood> tempFood = new ArrayList<CartFood>();
                tempFood.add(food);
                mFoodMap.put(key, tempFood);
            }
        }

        if (null != mFoodMap && mFoodMap.size() > 0) {
            mFoodsLayout.removeAllViews();
            Iterator iterator = mFoodMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                List<CartFood> cartFoods = (List<CartFood>) entry.getValue();

                if (null != cartFoods
                        && cartFoods.size() > 0) {
                    CartFood cartFood = cartFoods.get(0);
                    cartFood.quality = cartFoods.size();
                    CartFoodItemLayout layout = new CartFoodItemLayout(mContext);
                    layout.setListener(new CartFoodItemLayout.CartFoodItemListener() {
                        @Override
                        public void OnStatusChanged() {

                            if (null != mFoodsLayout
                                    && mFoodsLayout.getChildCount() > 0) {
                                final int count = mFoodsLayout.getChildCount();
                                boolean isSelected = true;
                                for (int i = 0; i < count; i++) {
                                    boolean childIsSelected = ((CartFoodItemLayout) mFoodsLayout.getChildAt(i)).getSelectedStatus();
                                    isSelected = isSelected && childIsSelected;
                                }

                                mIsSelected = isSelected;
                                mCheckIv.setImageResource(mIsSelected ?
                                        R.mipmap.ic_rect_check_checked : R.mipmap.ic_rect_check_normal);
                            }

                            if (null != mListener) {
                                mListener.OnStatusChanged();
                            }
                        }

                        @Override
                        public void OnDelete() {
                            if (null != mListener) {
                                mListener.OnDelete();
                            }
                        }

                        @Override
                        public void onNumChanged() {
                            if (null != mListener) {
                                mListener.onNumChanged();
                            }
                        }
                    });
                    layout.setData(cartFood);
                    mFoodsLayout.addView(layout);
                }
            }
        }
    }

    public BigDecimal getTotalPrice() {
        BigDecimal price = new BigDecimal(0.0);

        if (null != mFoodsLayout
                && mFoodsLayout.getChildCount() > 0) {
            final int count = mFoodsLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                BigDecimal childPrice = ((CartFoodItemLayout) mFoodsLayout.getChildAt(i)).getTotalPrice();
                if (null != childPrice) {
                    price = price.add(childPrice);
                }
            }
        }

        return price;
    }

    public List<CartFood> getSelectedFoods() {
        List<CartFood> selectedFoods = new ArrayList<CartFood>();
        if (null != mFoodsLayout
                && mFoodsLayout.getChildCount() > 0) {
            final int count = mFoodsLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                CartFood cartFood = ((CartFoodItemLayout) mFoodsLayout.getChildAt(i)).getSelectdFood();
                if (null != cartFood) {
                    selectedFoods.add(cartFood);
                }
            }
        }

        return selectedFoods;
    }

    public String getStoreId() {
        return mStoreId;
    }

    public interface CartStoreItemListener {

        void OnStatusChanged();

        void OnDelete();

        void onNumChanged();
    }
}
