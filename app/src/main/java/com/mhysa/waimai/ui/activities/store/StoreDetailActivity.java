package com.mhysa.waimai.ui.activities.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
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
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.model.cart.CartFood;
import com.mhysa.waimai.model.favorite.FavoriteState;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.store.FoodType;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.model.store.StoreFoodList;
import com.mhysa.waimai.ui.activities.cart.CartActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.store.FoodListAdapter;
import com.mhysa.waimai.ui.adapters.store.FoodTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 商家详情
 * Date: 2017/8/1
 *
 * @author xusheng
 */

public class StoreDetailActivity extends BaseActivity implements SectionIndexer {

    private static final String INTENT_STORE = "intent_store";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.layout_store_detail_header)
    RelativeLayout mStoreDetailHeader;

    @Bind(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.btn_work_off)
    Button mWorkOffBtn;

    @Bind(R.id.tv_work_time_pre)
    TextView mWorkTimePreTv;

    @Bind(R.id.tv_address_pre)
    TextView mAddressPreTv;

    @Bind(R.id.tv_desc_pre)
    TextView mDescPreTv;

    @Bind(R.id.tv_work_time)
    TextView mWorkTimeTv;

    @Bind(R.id.tv_address)
    TextView mAddressTv;

    @Bind(R.id.tv_desc)
    TextView mDescTv;

    @Bind(R.id.iv_favorire)
    ImageView mFavoriteIv;

    @Bind(R.id.tv_favorite)
    TextView mFavoriteTv;

    @Bind(R.id.tv_category_number)
    TextView mCartNumTv;

    @Bind(R.id.lv_type)
    ListView mTypeLv;

    @Bind(R.id.lv_foods)
    ListView mFoodsLv;

    @Bind(R.id.layout_title)
    LinearLayout mCategoryTitleLayout;

    @Bind(R.id.tv_category_name)
    TextView mCategoryNameTv;

    @Bind(R.id.layout_category)
    RelativeLayout mCategoryLayout;

    private FoodTypeAdapter mTypeAdapter;

    private FoodListAdapter mFoodAdapter;

    private List<FoodType> mFoodType = new ArrayList<FoodType>();

    private List<Food> mFoods = new ArrayList<Food>();

    private Store mStore;

    // 上次选中的左侧菜单
    private View mLastView;

    // 上次第一个可见元素，用于滚动时记录标识。
    private int mLastFirstVisibleItem = -1;

    private int mCartFoodSize = 0;

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
                            mCartNumTv.setVisibility(View.VISIBLE);
                            mCartNumTv.setText(mCartFoodSize + "");
                        } else {
                            mCartNumTv.setVisibility(View.GONE);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    break;
                }

                case CartManager.GET_FOODS_BY_STORE_SUCCESS: {
                    try {
                        HashMap<String, List<CartFood>> map = (HashMap<String, List<CartFood>>) msg.obj;
                        if (null != map && null != mFoodAdapter) {
                            mFoodAdapter.setCartFoods(map);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    break;
                }

            }
        }
    };

    @Override
    protected void getIntentData() {
        super.getIntentData();

        if (null != getIntent()) {
            mStore = (Store) getIntent().getSerializableExtra(INTENT_STORE);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_store_detail;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.store_detail);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mLastView) {
                    mLastView.setBackgroundColor(Color.parseColor("#f4f4f4"));
                    TextView textView = (TextView) mLastView.findViewById(R.id.tv_type);
                    textView.setTextColor(Color.parseColor("#808080"));
                }

                // 设置选中颜色为白色
                view.setBackgroundColor(Color.parseColor("#ffffff"));
                TextView textView1 = (TextView) view.findViewById(R.id.tv_type);
                textView1.setTextColor(Color.parseColor("#333333"));

                // 点击左侧，右侧滚动到对应的位置
                TextView section_tv = (TextView) view.findViewById(R.id.tv_section);
                int location = mFoodAdapter.getPositionForSection(
                        (Integer.parseInt(section_tv.getText().toString())));
                if (location != -1) {
                    mFoodsLv.setSelection(location);
                }
                mLastView = view;
            }
        });

        mFoodsLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    //获取屏幕第一个item的section
                    int section = getSectionForPosition(firstVisibleItem);
                    int nextSection = 0;
                    if (mFoods.size() > 1) {
                        nextSection = getSectionForPosition(firstVisibleItem + 1);
                    }

                    int nextSecPosition = getPositionForSection(+nextSection);
                    if (firstVisibleItem != mLastFirstVisibleItem) {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mCategoryTitleLayout
                                .getLayoutParams();
                        params.topMargin = 0;
                        mCategoryTitleLayout.setLayoutParams(params);

                        if (Utils.isChinese(StoreDetailActivity.this)) {
                            mCategoryNameTv.setText(mFoods.get(getPositionForSection(section)).type_cn);
                        } else {
                            mCategoryNameTv.setText(mFoods.get(getPositionForSection(section)).type_en);
                        }

                        if (mLastView != null) {
                            mLastView.setBackgroundColor(Color.parseColor("#f4f4f4"));
                            TextView textView = (TextView) mLastView.findViewById(R.id.tv_type);
                            textView.setTextColor(Color.parseColor("#808080"));
                        }

                        mLastView = mTypeLv.getChildAt(section);
                        // 设置选中颜色为白色
                        mLastView.setBackgroundColor(Color.parseColor("#ffffff"));
                        TextView textView1 = (TextView) mLastView.findViewById(R.id.tv_type);
                        textView1.setTextColor(Color.parseColor("#333333"));

                    }
                    if (nextSecPosition == firstVisibleItem + 1) {
                        View childView = view.getChildAt(0);
                        if (childView != null) {
                            int titleHeight = mCategoryTitleLayout.getHeight();
                            int bottom = childView.getBottom();
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mCategoryTitleLayout
                                    .getLayoutParams();
                            if (bottom < titleHeight) {
                                float pushedDistance = bottom - titleHeight;
                                params.topMargin = (int) pushedDistance;
                                mCategoryTitleLayout.setLayoutParams(params);
                            } else {
                                if (params.topMargin != 0) {
                                    params.topMargin = 0;
                                    mCategoryTitleLayout.setLayoutParams(params);
                                }
                            }
                        }
                    }
                    mLastFirstVisibleItem = firstVisibleItem;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                            CartActivity.invoke(StoreDetailActivity.this);
                        }
                        break;
                    }
                }
                return true;
            }
        });
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

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        EventBus.getDefault().register(this);

        mTypeAdapter = new FoodTypeAdapter(this, mFoodType);
        mFoodAdapter = new FoodListAdapter(this, mFoods);
        mFoodAdapter.setListener(new FoodListAdapter.CartListener() {
            @Override
            public void add(int number) {
                mCartFoodSize = mCartFoodSize + number;
                if (mCartFoodSize > 0) {
                    mCartNumTv.setVisibility(View.VISIBLE);
                    mCartNumTv.setText(mCartFoodSize + "");
                } else {
                    mCartNumTv.setVisibility(View.GONE);
                }

                getCartFoodByStoreId();
            }

            @Override
            public void delete() {
                mCartFoodSize = mCartFoodSize - 1;

                if (mCartFoodSize > 0) {
                    mCartNumTv.setVisibility(View.VISIBLE);
                    mCartNumTv.setText(mCartFoodSize + "");
                } else {
                    mCartNumTv.setVisibility(View.GONE);
                }

                getCartFoodByStoreId();
            }
        });

        mTypeLv.setAdapter(mTypeAdapter);
        mFoodsLv.setAdapter(mFoodAdapter);

        getStoreDetail();
        getStoreFoodList();
        getAllCartFoodSize();
    }

    @OnClick({R.id.layout_back, R.id.layout_favorite, R.id.layout_category})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                StoreDetailActivity.this.finish();
                break;
            }

            case R.id.layout_favorite: {
                // 收藏或者取消收藏
                if (StringUtils.getInstance().isNullOrEmpty(Utils.getToken(StoreDetailActivity.this))) {
                    LoginActivity.invoke(StoreDetailActivity.this, false);
                } else {
                    handleFavorite();
                }

                break;
            }

            case R.id.layout_category: {
                CartActivity.invoke(StoreDetailActivity.this);
                break;
            }

            default: {
                break;
            }
        }
    }

    /**
     * 获取
     */
    private void getStoreDetail() {
        showNetDialog("");
        OkHttpClientManager.getInstance().getStoreDetail(Utils.getUserId(this),
                Utils.getToken(this),
                StringUtils.getInstance().isNullOrEmpty(mStore.store_id) ? "" : mStore.store_id,
                new WtNetWorkListener<Store>() {
                    @Override
                    public void onSucess(RemoteReturnData<Store> data) {
                        if (null != data && null != data.data) {
                            drawviews(data.data);
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(StoreDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                });
    }

    private void drawviews(Store store) {
        mFoodAdapter.setStore(store);
        mStore = store;

        ImageUtils.getInstance().setImageURL(store.store_image, mAvatarSdv);

        if (Utils.isChinese(this)) {
            StringUtils.getInstance().setText(store.store_name_cn, mNameTv);
            StringUtils.getInstance().setText(store.store_address_cn, mAddressTv);
            StringUtils.getInstance().setText(store.business_hours_cn, mWorkTimeTv);
            StringUtils.getInstance().setText(store.store_desc_cn, mDescTv);
        } else {
            StringUtils.getInstance().setText(store.store_name_en, mNameTv);
            StringUtils.getInstance().setText(store.store_address_en, mAddressTv);
            StringUtils.getInstance().setText(store.business_hours_en, mWorkTimeTv);
            StringUtils.getInstance().setText(store.store_desc_en, mDescTv);
        }

        if (store.has_collect == FusionCode.FavoriteState.FAVORITE_STATE_FAVORITE) {
            // 已经收藏
            mFavoriteIv.setImageResource(R.mipmap.ic_store_favorite_pressed);
            mFavoriteTv.setTextColor(Color.parseColor("#2196f3"));
            mFavoriteTv.setText(R.string.cancel);
        } else {
            // 未收藏
            mFavoriteIv.setImageResource(R.mipmap.ic_store_favorite_normal);
            mFavoriteTv.setTextColor(Color.parseColor("#808080"));
            mFavoriteTv.setText(R.string.store_favorite);
        }

        if (!StringUtils.getInstance().isNullOrEmpty(store.onwork)
                && store.onwork.equals(FusionCode.WorkStatus.WORK_STATUS_OFFWORK)) {
            mStoreDetailHeader.setBackgroundColor(Color.parseColor("#E83C3C"));
            mWorkOffBtn.setVisibility(View.VISIBLE);
            mNameTv.setTextColor(Color.parseColor("#ffffff"));
            mWorkTimePreTv.setTextColor(Color.parseColor("#ffffff"));
            mWorkTimeTv.setTextColor(Color.parseColor("#ffffff"));
            mAddressPreTv.setTextColor(Color.parseColor("#ffffff"));
            mAddressTv.setTextColor(Color.parseColor("#ffffff"));
            mDescPreTv.setTextColor(Color.parseColor("#ffffff"));
            mDescTv.setTextColor(Color.parseColor("#ffffff"));
        } else {
            mStoreDetailHeader.setBackgroundColor(Color.parseColor("#00000000"));
            mWorkOffBtn.setVisibility(View.GONE);
            mNameTv.setTextColor(Color.parseColor("#333333"));
            mWorkTimePreTv.setTextColor(Color.parseColor("#808080"));
            mWorkTimeTv.setTextColor(Color.parseColor("#808080"));
            mAddressPreTv.setTextColor(Color.parseColor("#808080"));
            mAddressTv.setTextColor(Color.parseColor("#808080"));
            mDescPreTv.setTextColor(Color.parseColor("#808080"));
            mDescTv.setTextColor(Color.parseColor("#808080"));
        }
    }

    private void handleFavorite() {
        showNetDialog("");
        OkHttpClientManager.getInstance().collectOrUnCollect(
                Utils.getUserId(this),
                Utils.getToken(this),
                StringUtils.getInstance().isNullOrEmpty(mStore.store_id) ? "" : mStore.store_id,
                new WtNetWorkListener<FavoriteState>() {
                    @Override
                    public void onSucess(RemoteReturnData<FavoriteState> data) {
                        if (null != data && null != data.data) {
                            FavoriteState state = data.data;
                            mStore.has_collect = state.collection_state;
                            if (state.collection_state == FusionCode.FavoriteState.FAVORITE_STATE_FAVORITE) {
                                // 已经收藏
                                EventBus.getDefault().post(new ApplicationEvent(EventID.ADD_STORE_FAVORITE, mStore));
                                mFavoriteIv.setImageResource(R.mipmap.ic_store_favorite_pressed);
                                mFavoriteTv.setTextColor(Color.parseColor("#2196f3"));
                                mFavoriteTv.setText(R.string.cancel);

                                PromptUtils.getInstance().showShortPromptToast(StoreDetailActivity.this,
                                        R.string.favorite_success);
                            } else {
                                // 未收藏
                                EventBus.getDefault().post(new ApplicationEvent(EventID.DELETE_STORE_FAVORITE, mStore));
                                mFavoriteIv.setImageResource(R.mipmap.ic_store_favorite_normal);
                                mFavoriteTv.setTextColor(Color.parseColor("#808080"));
                                mFavoriteTv.setText(R.string.store_favorite);

                                PromptUtils.getInstance().showShortPromptToast(StoreDetailActivity.this,
                                        R.string.delete_favorite_success);
                            }
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(StoreDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                }
        );
    }

    private void getStoreFoodList() {
        OkHttpClientManager.getInstance().getStoreFoodList(Utils.getUserId(this),
                Utils.getToken(this), mStore.store_id,
                new WtNetWorkListener<StoreFoodList>() {
                    @Override
                    public void onSucess(RemoteReturnData<StoreFoodList> data) {
                        if (null != data && null != data.data) {
                            drawFoodList(data.data);
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(StoreDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    private void drawFoodList(StoreFoodList foodList) {
        if (null != foodList
                && null != foodList.type_list
                && foodList.type_list.size() > 0) {
            mFoodType.clear();
            mFoods.clear();

            List<FoodType> foodTypes = foodList.type_list;
            mFoodType.addAll(foodTypes);

            final int size = foodTypes.size();

            for (int i = 0; i < size; i++) {
                FoodType foodType = foodTypes.get(i);
                if (null != foodType
                        && null != foodType.foods_list
                        && foodType.foods_list.size() > 0) {
                    List<Food> foods = foodType.foods_list;
                    final int foodSize = foods.size();
                    for (int j = 0; j < foodSize; j++) {
                        Food food = foods.get(j);
                        food.selectedId = i;
                        food.type_cn = foodType.type_name_cn;
                        food.type_en = foodType.type_name_en;

                        mFoods.add(food);
                    }
                }
            }

            mFoodAdapter.notifyDataSetChanged();
            mTypeAdapter.notifyDataSetChanged();

            // 从数据库获取用户购物车数据
            getCartFoodByStoreId();
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < mFoods.size(); i++) {
            int section = mFoods.get(i).selectedId;
            if (section == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        if (null != mFoods && mFoods.size() > i) {
            return mFoods.get(i).selectedId;
        }
        return 0;
    }

    private void getAllCartFoodSize() {
        CartManager.getInstance().getAllFoods(mHandler);
    }

    private void getCartFoodByStoreId() {
        if (null == mStore
                || StringUtils.getInstance().isNullOrEmpty(mStore.store_id)) {
            return;
        }

        CartManager.getInstance().getCartFoodByStore(mHandler, mStore.store_id);
    }

    private boolean mIsFirstIn = true;

    @Override
    protected void onResume() {
        super.onResume();
        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);

        if (mIsFirstIn) {
            mIsFirstIn = false;
        }

        if (!mIsFirstIn) {
            getAllCartFoodSize();
            getCartFoodByStoreId();
        }
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
                StoreDetailActivity.this.finish();
                break;
            }
        }
    }

    public static void invoke(Context context, Store store) {

        if (null == context
                || null == store) {
            return;
        }

        Intent intent = new Intent(context, StoreDetailActivity.class);
        intent.putExtra(INTENT_STORE, store);
        context.startActivity(intent);
    }
}
