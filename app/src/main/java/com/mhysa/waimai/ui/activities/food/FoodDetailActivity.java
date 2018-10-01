package com.mhysa.waimai.ui.activities.food;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.listview.CustomerListView;
import com.joey.devilfish.widget.scrollview.ObservableScrollView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.manager.SpecManager;
import com.mhysa.waimai.model.cart.CartFood;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.food.FoodDetail;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.activities.cart.CartActivity;
import com.mhysa.waimai.ui.adapters.food.FoodContentAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/8/8
 *
 * @author xusheng
 */

public class FoodDetailActivity extends BaseActivity {

    private static final String INTENT_FOOD = "intent_food";

    @Bind(R.id.tv_title_white)
    TextView mWhiteTitleTv;

    @Bind(R.id.tv_title_black)
    TextView mBlackTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.iv_back)
    ImageView mBackIv;

    @Bind(R.id.iv_back_white)
    ImageView mBackWhiteIv;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.scrollview)
    ObservableScrollView mScrollView;

    @Bind(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @Bind(R.id.iv_radius)
    ImageView mRadiusIv;

    @Bind(R.id.tv_food_name)
    TextView mFoodNameTv;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.iv_add)
    ImageView mAddIv;

    @Bind(R.id.tv_number)
    TextView mNumberTv;

    @Bind(R.id.iv_minus)
    ImageView mMinusIv;

    @Bind(R.id.lv_content)
    CustomerListView mContentLv;

    @Bind(R.id.layout_category)
    RelativeLayout mCategoryLayout;

    @Bind(R.id.tv_category_number)
    TextView mCategoryNumberTv;

    private boolean mIsDialogShowing = false;

    private Food mFood;

    private List<FoodDetail> mFoodDetails = new ArrayList<FoodDetail>();

    private FoodContentAdapter mAdapter;

    private int mOldY = 0;

    private int mCartFoodSize = 0;

    // 购物车中该商品的数量
    private int mFoodCount = 0;

    private int mLastRawX = 0;

    private int mLastRawY = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CartManager.GET_ALL_FOOD_SUCCESS: {
                    try {
                        List<CartFood> foods = (List<CartFood>) msg.obj;
                        if (null != foods && foods.size() > 0) {
                            mCartFoodSize = foods.size();
                            mCategoryNumberTv.setVisibility(View.VISIBLE);
                            mCategoryNumberTv.setText(mCartFoodSize + "");

                            final int size = foods.size();
                            for (int i = 0; i < size; i++) {
                                CartFood cartFood = foods.get(i);
                                if (null != cartFood
                                        && cartFood.foods_id.equals(mFood.foods_id)
                                        && cartFood.store_id.equals(mFood.store_id)) {
                                    mFoodCount = mFoodCount + 1;
                                }
                            }

                            if (mFoodCount > 0) {
                                mNumberTv.setVisibility(View.VISIBLE);
                                mMinusIv.setVisibility(View.VISIBLE);

                                mNumberTv.setText("" + mFoodCount);
                            } else {
                                mNumberTv.setVisibility(View.GONE);
                                mMinusIv.setVisibility(View.GONE);
                            }
                        } else {
                            mCategoryNumberTv.setVisibility(View.GONE);
                            mNumberTv.setVisibility(View.GONE);
                            mMinusIv.setVisibility(View.GONE);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    break;
                }

                case CartManager.ADD_FOOD_TO_CART_SUCCESS: {
                    int number = (int) msg.obj;
                    mCartFoodSize = mCartFoodSize + number;
                    mCategoryNumberTv.setVisibility(View.VISIBLE);
                    mCategoryNumberTv.setText(mCartFoodSize + "");

                    mFoodCount = mFoodCount + number;
                    mNumberTv.setVisibility(View.VISIBLE);
                    mMinusIv.setVisibility(View.VISIBLE);
                    mNumberTv.setText("" + mFoodCount);
                    break;
                }

                case CartManager.DELETE_FOOD_SUCCESS: {
                    mCartFoodSize = mCartFoodSize - 1;
                    if (mCartFoodSize > 0) {
                        mCategoryNumberTv.setVisibility(View.VISIBLE);
                        mCategoryNumberTv.setText(mCartFoodSize + "");
                    } else {
                        mCategoryNumberTv.setVisibility(View.GONE);
                    }

                    mFoodCount = mFoodCount - 1;
                    if (mFoodCount > 0) {
                        mNumberTv.setVisibility(View.VISIBLE);
                        mMinusIv.setVisibility(View.VISIBLE);

                        mNumberTv.setText("" + mFoodCount);
                    } else {
                        mNumberTv.setVisibility(View.GONE);
                        mMinusIv.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_food_detail;
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mFood = (Food) getIntent().getSerializableExtra(INTENT_FOOD);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        mHeaderLayout.getBackground().setAlpha(0);
        mViewDivider.getBackground().setAlpha(0);

        mWhiteTitleTv.setTextColor(Color.argb(255, 255, 255, 255));
        mBlackTitleTv.setTextColor(Color.argb(0, 26, 26, 26));

        mBackWhiteIv.setAlpha(1.0f);
        mBackIv.setAlpha(0.0f);

        mAdapter = new FoodContentAdapter(FoodDetailActivity.this, mFoodDetails);
        mContentLv.setAdapter(mAdapter);

        mScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                mOldY = y;
                scrollChange();
            }
        });

        mCategoryLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mLastRawX = (int) event.getRawX();
                        mLastRawY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (Math.abs(mLastRawX - event.getRawX()) > 10 &&
                                Math.abs(mLastRawY - event.getRawY()) > 10) {
//                            moveViewWithFinger(v, event.getRawX(), event.getRawY());
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (Math.abs(mLastRawX - event.getRawX()) < 10 &&
                                Math.abs(mLastRawY - event.getRawY()) < 10) {
                            CartActivity.invoke(FoodDetailActivity.this);
                        }
                        break;
                    }
                }
                return true;
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                AppConfig.getScreenWidth() * 420 / 750);
        mAvatarSdv.setLayoutParams(params);
        mRadiusIv.setLayoutParams(params);
    }

    private void moveViewWithFinger(View view, float rawX, float rawY) {
        int minY = ExtendUtils.getInstance().dip2px(this, 50);
        int maxY = AppConfig.getScreenHeight() - 3 * mCategoryLayout.getHeight() / 2;

        if (rawY <= minY) {
            rawY = minY;
        }

        if (rawY >= maxY) {
            rawY = maxY;
        }

        int minX = mCategoryLayout.getWidth() / 2;
        int maxX = AppConfig.getScreenWidth() - mCategoryLayout.getWidth();

        if (rawX <= minX) {
            rawX = minX;
        }

        if (rawX >= maxX) {
            rawX = maxX;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (rawX == minX) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = (int) rawX;
        }

        if (rawY == maxY) {
            params.topMargin = (int) rawY - ExtendUtils.getInstance().dip2px(this, 50);
        } else {
            params.topMargin = (int) rawY - minY;
        }

        view.setLayoutParams(params);
    }

    private void scrollChange() {
        int imageHeight = ExtendUtils.getInstance().dip2px(FoodDetailActivity.this, 150);
        int tempHeight = imageHeight / 2;

        if (mOldY > imageHeight) {
            mHeaderLayout.getBackground().setAlpha(255);
            mViewDivider.getBackground().setAlpha(255);
        } else {
            mHeaderLayout.getBackground().setAlpha(mOldY * 255 / imageHeight);
            mViewDivider.getBackground().setAlpha(mOldY * 255 / imageHeight);

            if (mOldY > tempHeight) {
                mBackIv.setAlpha(mOldY * 1.0f / tempHeight);
                mBlackTitleTv.setTextColor(Color.argb(mOldY * 255 / tempHeight, 26, 26, 26));

                mBackIv.setVisibility(View.VISIBLE);
                mBackWhiteIv.setVisibility(View.GONE);
                mBlackTitleTv.setVisibility(View.VISIBLE);
                mWhiteTitleTv.setVisibility(View.GONE);
            } else {
                mBackWhiteIv.setAlpha((tempHeight - mOldY) * 1.0f / tempHeight);
                mWhiteTitleTv.setTextColor(Color.argb((tempHeight - mOldY) * 255 / tempHeight, 255, 255, 255));

                mBackIv.setVisibility(View.GONE);
                mBackWhiteIv.setVisibility(View.VISIBLE);
                mBlackTitleTv.setVisibility(View.GONE);
                mWhiteTitleTv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        EventBus.getDefault().register(this);

        showNetDialog("");
        OkHttpClientManager.getInstance().getFoodDetail(Utils.getUserId(this),
                Utils.getToken(this), mFood.store_id, mFood.foods_id, new WtNetWorkListener<Food>() {
                    @Override
                    public void onSucess(RemoteReturnData<Food> data) {
                        if (null != data && null != data.data) {
                            mFood = data.data;
                            drawViews();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {

                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                });
    }

    private void drawViews() {
        if (Utils.isChinese(this)) {
            ImageUtils.getInstance().setImageURL(mFood.foods_image_cn, mAvatarSdv);
            StringUtils.getInstance().setText(mFood.foods_name_cn, mFoodNameTv);
        } else {
            ImageUtils.getInstance().setImageURL(mFood.foods_image_en, mAvatarSdv);
            StringUtils.getInstance().setText(mFood.foods_name_en, mFoodNameTv);
        }

        mPriceTv.setText(getResources().getString(R.string.food_price, mFood.price));

        if (null != mFood.foods_detail && mFood.foods_detail.size() > 0) {
            mFoodDetails.clear();
            mFoodDetails.addAll(mFood.foods_detail);
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.layout_back, R.id.layout_category,
            R.id.iv_add, R.id.iv_minus})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                FoodDetailActivity.this.finish();
                break;
            }

            case R.id.layout_category: {
                CartActivity.invoke(FoodDetailActivity.this);
                break;
            }

            case R.id.iv_add: {
                if (!StringUtils.getInstance().isNullOrEmpty(mFood.has_spec)
                        && mFood.has_spec.equals(FusionCode.FoodSpec.HAS_SPEC)
                        && null != mFood.spec_class_list
                        && mFood.spec_class_list.size() > 0) {
                    if (!mIsDialogShowing) {
                        // 多规格
                        mIsDialogShowing = true;
                        SpecManager.getInstance().showSpecDialog(FoodDetailActivity.this, mFood.spec_class_list
                                , mFood, mFood.store, mSpecChooseListener);
                    }
                } else {
                    // 无多规格
                    CartFood cartFood = new CartFood();
                    cartFood.foods_id = mFood.foods_id;
                    cartFood.foods_name_cn = mFood.foods_name_cn;
                    cartFood.foods_name_en = mFood.foods_name_en;
                    cartFood.foods_image_cn = mFood.foods_image_cn;
                    cartFood.foods_image_en = mFood.foods_image_en;
                    cartFood.price = mFood.price;
                    cartFood.spec_id_list = "";
                    cartFood.specials_name_cn = "";
                    cartFood.specials_name_en = "";
                    cartFood.store_name_cn = mFood.store.store_name_cn;
                    cartFood.store_name_en = mFood.store.store_name_en;
                    cartFood.store_image_cn = mFood.store.store_image;
                    cartFood.store_image_en = mFood.store.store_image;
                    cartFood.store_id = mFood.store.store_id;

                    CartManager.getInstance().addFood(cartFood, mHandler);
                }
                break;
            }

            case R.id.iv_minus: {
                if (!StringUtils.getInstance().isNullOrEmpty(mFood.has_spec)
                        && mFood.has_spec.equals(FusionCode.FoodSpec.HAS_SPEC)
                        && null != mFood.spec_class_list
                        && mFood.spec_class_list.size() > 0) {
                    // 多规格
                    if (mFoodCount == 1) {
                        CartManager.getInstance().deleteFood(mHandler, mFood.foods_id, mFood.store.store_id, "");
                    } else {
                        PromptUtils.getInstance().showShortPromptToast(FoodDetailActivity.this, R.string.delete_in_cart);
                    }
                } else {
                    // 无多规格
                    CartManager.getInstance().deleteFood(mHandler, mFood.foods_id, mFood.store_id, "");
                }
                break;
            }

            default: {
                break;
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        scrollChange();

        mFoodCount = 0;
        mCartFoodSize = 0;
        // 获取购物车的数量
        getAllCartFoodSize();
    }

    private void getAllCartFoodSize() {
        CartManager.getInstance().getAllFoods(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        if (null == event) {
            return;
        }

        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.PAY_SUCCESS: {
                FoodDetailActivity.this.finish();
                break;
            }
        }
    }

    public static void invoke(Context context, Food food) {
        Intent intent = new Intent(context, FoodDetailActivity.class);
        intent.putExtra(INTENT_FOOD, food);
        context.startActivity(intent);
    }
}
