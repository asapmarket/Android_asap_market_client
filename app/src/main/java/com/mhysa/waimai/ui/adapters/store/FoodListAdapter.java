package com.mhysa.waimai.ui.adapters.store;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.manager.SpecManager;
import com.mhysa.waimai.model.cart.CartFood;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.activities.food.FoodDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * food adapter
 * Date: 2017/8/2
 *
 * @author xusheng
 */

public class FoodListAdapter extends BaseAdapter implements SectionIndexer {

    private Context mContext;

    private List<Food> mFoods = new ArrayList<Food>();

    private HashMap<String, List<CartFood>> mCartFoods = new HashMap<String, List<CartFood>>();

    private Store mStore;

    private CartListener mListener;

    private boolean mIsDialogShowing = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CartManager.ADD_FOOD_TO_CART_SUCCESS: {
                    int number = (int) msg.obj;
                    if (null != mListener) {
                        mListener.add(number);
                    }
                    break;
                }

                case CartManager.DELETE_FOOD_SUCCESS: {
                    if (null != mListener) {
                        mListener.delete();
                    }
                    break;
                }
            }
        }
    };

    public FoodListAdapter(Context context, List<Food> foods) {
        this.mContext = context;
        this.mFoods = foods;
    }

    /**
     * 设置购物车商品
     *
     * @param cartFoods
     */
    public void setCartFoods(HashMap<String, List<CartFood>> cartFoods) {
        if (null != cartFoods) {
            mCartFoods = cartFoods;
            this.notifyDataSetChanged();
        }
    }

    public void setStore(Store store) {
        this.mStore = store;
        this.notifyDataSetChanged();
    }

    public void setListener(CartListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {

        if (null != mFoods) {
            return mFoods.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mFoods && mFoods.size() > position) {
            return mFoods.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_food, null);

            holder.mTitleLayout = (LinearLayout) convertView.findViewById(R.id.layout_title);
            holder.mCategoryNameTv = (TextView) convertView.findViewById(R.id.tv_category_name);
            holder.mAvatarSdv = (SimpleDraweeView) convertView.findViewById(R.id.sdv_avatar);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mDesTv = (TextView) convertView.findViewById(R.id.tv_description);
            holder.mPriceTv = (TextView) convertView.findViewById(R.id.tv_price);
            holder.mAddIv = (ImageView) convertView.findViewById(R.id.iv_add);
            holder.mMinusIv = (ImageView) convertView.findViewById(R.id.iv_minus);
            holder.mNumberTv = (TextView) convertView.findViewById(R.id.tv_number);
            holder.mDividerView = (View) convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Food food = mFoods.get(position);
        if (null != food) {
            holder.mDividerView.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

            //如果有该类型，则隐藏
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                holder.mTitleLayout.setVisibility(View.GONE);
                if (Utils.isChinese(mContext)) {
                    StringUtils.getInstance().setText(food.type_cn, holder.mCategoryNameTv);
                } else {
                    StringUtils.getInstance().setText(food.type_en, holder.mCategoryNameTv);
                }

            } else {
                holder.mTitleLayout.setVisibility(View.GONE);
            }

            if (Utils.isChinese(mContext)) {
                ImageUtils.getInstance().setImageURL(food.foods_image_cn, holder.mAvatarSdv);
                StringUtils.getInstance().setText(food.foods_name_cn, holder.mNameTv);
                StringUtils.getInstance().setText(food.foods_desc_cn, holder.mDesTv);
            } else {
                ImageUtils.getInstance().setImageURL(food.foods_image_en, holder.mAvatarSdv);
                StringUtils.getInstance().setText(food.foods_name_en, holder.mNameTv);
                StringUtils.getInstance().setText(food.foods_desc_en, holder.mDesTv);
            }

            StringUtils.getInstance().setText("$" + food.price, holder.mPriceTv);

            final String foodId = food.foods_id;
            if (mCartFoods.containsKey(foodId)) {
                List<CartFood> foods = mCartFoods.get(foodId);
                if (null != foods && foods.size() > 0) {
                    int size = foods.size();
                    holder.mNumberTv.setVisibility(View.VISIBLE);
                    holder.mMinusIv.setVisibility(View.VISIBLE);

                    holder.mNumberTv.setText(size + "");
                } else {
                    holder.mNumberTv.setVisibility(View.GONE);
                    holder.mMinusIv.setVisibility(View.GONE);
                }
            } else {
                holder.mNumberTv.setVisibility(View.GONE);
                holder.mMinusIv.setVisibility(View.GONE);
            }

            holder.mAddIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtils.getInstance().isNullOrEmpty(food.has_spec)
                            && food.has_spec.equals(FusionCode.FoodSpec.HAS_SPEC)
                            && null != food.spec_class_list
                            && food.spec_class_list.size() > 0) {
                        if (!mIsDialogShowing) {
                            mIsDialogShowing = true;
                            SpecManager.getInstance().showSpecDialog(mContext, food.spec_class_list
                                    , food, mStore, mSpecChooseListener);
                        }
                    } else {
                        CartFood cartFood = new CartFood();
                        cartFood.foods_id = foodId;
                        cartFood.foods_name_cn = food.foods_name_cn;
                        cartFood.foods_name_en = food.foods_name_en;
                        cartFood.foods_image_cn = food.foods_image_cn;
                        cartFood.foods_image_en = food.foods_image_en;
                        cartFood.price = food.price;
                        cartFood.spec_id_list = "";
                        cartFood.specials_name_cn = "";
                        cartFood.specials_name_en = "";
                        cartFood.store_name_cn = mStore.store_name_cn;
                        cartFood.store_name_en = mStore.store_name_en;
                        cartFood.store_image_cn = mStore.store_image;
                        cartFood.store_image_en = mStore.store_image;
                        cartFood.store_id = mStore.store_id;

                        CartManager.getInstance().addFood(cartFood, mHandler);
                    }

                }
            });

            holder.mMinusIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtils.getInstance().isNullOrEmpty(food.has_spec)
                            && food.has_spec.equals(FusionCode.FoodSpec.HAS_SPEC)
                            && null != food.spec_class_list
                            && food.spec_class_list.size() > 0) {

                        if (mCartFoods.containsKey(foodId)) {
                            List<CartFood> foods = mCartFoods.get(foodId);
                            if (foods.size() == 1) {
                                CartManager.getInstance().deleteFood(mHandler, food.foods_id, mStore.store_id, "");
                            } else {
                                PromptUtils.getInstance().showShortPromptToast(mContext, R.string.delete_in_cart);
                            }
                        }
                    } else {
                        CartManager.getInstance().deleteFood(mHandler, food.foods_id, mStore.store_id, "");
                    }

                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FoodDetailActivity.invoke(mContext, food);
                }
            });
        }

        return convertView;
    }

    private SpecManager.OnSpecChooseListener mSpecChooseListener = new SpecManager.OnSpecChooseListener() {
        @Override
        public void onSpecChoose(String ids, String specials_name_cn,
                                 String specials_name_en, int count, Food food, Store store, String price) {
            mIsDialogShowing = false;
            if (count == 0
                    || StringUtils.getInstance().isNullOrEmpty(ids)) {
                return;
            }

            List<CartFood> cartFoods = new ArrayList<CartFood>();
            for (int i = 0; i < count; i++) {
                CartFood cartFood = new CartFood();
                cartFood.foods_id = food.foods_id;
                cartFood.foods_name_cn = food.foods_name_cn;
                cartFood.foods_name_en = food.foods_name_en;
                cartFood.foods_image_cn = food.foods_image_cn;
                cartFood.foods_image_en = food.foods_image_en;
                cartFood.price = price;
                cartFood.spec_id_list = ids;
                cartFood.specials_name_cn = specials_name_cn;
                cartFood.specials_name_en = specials_name_en;
                cartFood.store_name_cn = store.store_name_cn;
                cartFood.store_name_en = store.store_name_en;
                cartFood.store_image_cn = store.store_image;
                cartFood.store_image_en = store.store_image;
                cartFood.store_id = store.store_id;

                cartFoods.add(cartFood);
            }

            CartManager.getInstance().addFoods(cartFoods, mHandler);
        }

        @Override
        public void onCancel() {
            mIsDialogShowing = false;
        }
    };

    class ViewHolder {
        LinearLayout mTitleLayout;
        TextView mCategoryNameTv;
        SimpleDraweeView mAvatarSdv;
        TextView mNameTv;
        TextView mDesTv;
        TextView mPriceTv;
        ImageView mAddIv;
        TextView mNumberTv;
        ImageView mMinusIv;
        View mDividerView;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mFoods.get(position).selectedId;
    }

    /**
     * 查询api可以得知
     * 根据分类列的索引号获得该序列的首个位置
     *
     * @param sectionIndex
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        final int size = getCount();
        for (int i = 0; i < size; i++) {
            int section = mFoods.get(i).selectedId;
            if (section == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        // TODO 自动生成的方法存根
        return null;
    }

    public interface CartListener {

        void add(int number);

        void delete();
    }
}
