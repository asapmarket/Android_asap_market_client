package com.mhysa.waimai.ui.activities.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.WaimaiApplication;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.model.cart.CartFood;
import com.mhysa.waimai.ui.activities.cart.CartActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.main.MainPageAdapter;
import com.mhysa.waimai.ui.customerviews.MainBottomLayout;
import com.mhysa.waimai.ui.fragments.favorite.FavoriteFragment;
import com.mhysa.waimai.ui.fragments.home.HomeFragment;
import com.mhysa.waimai.ui.fragments.order.OrderFragment;
import com.mhysa.waimai.ui.fragments.personal.PersonalFragment;
import com.mhysa.waimai.ui.fragments.wallet.WalletFragment;
import com.mhysa.waimai.utils.PromptDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    private static final int MAX_BACK_DURATION = 2000;

    private long mLastBackPressTime;

    private MainBottomLayout mBottomLayout;

    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private MainPageAdapter mAdapter;

    private HomeFragment mHomeFragment;

    private FavoriteFragment mFavoriteFragment;

    private OrderFragment mOrderFragment;

    private PersonalFragment mPersonalFragment;

    private WalletFragment mWalletFragment;

    private Dialog mLoginTipDialog;

    @Bind(R.id.layout_category)
    RelativeLayout mCategoryLayout;

    @Bind(R.id.tv_category_number)
    TextView mCategoryNumberTv;

    @Bind(R.id.iv_bottom_cart)
    ImageView mBottomCartIv;

    @Bind(R.id.tv_cate_num)
    TextView mCateNumTv;

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
                            mCategoryNumberTv.setVisibility(View.VISIBLE);
                            mCategoryNumberTv.setText(mCartFoodSize + "");
                            mCateNumTv.setVisibility(View.VISIBLE);
                            if (mCartFoodSize > 99) {
                                mCateNumTv.setText("99+");
                            } else {
                                mCateNumTv.setText(mCartFoodSize + "");
                            }

                            relayoutCartNum();
                        } else {
                            mCategoryNumberTv.setVisibility(View.GONE);
                            mCateNumTv.setVisibility(View.GONE);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    break;
                }

            }
        }
    };

    private void relayoutCartNum() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCateNumTv.getLayoutParams();
        if (null != mCateNumTv) {
            if (mCartFoodSize > 0 && mCartFoodSize < 10) {
                mCateNumTv.setBackgroundResource(R.drawable.bg_circle_ff0000);
                params.width = ExtendUtils.getInstance().dip2px(this, 19);
                params.height = ExtendUtils.getInstance().dip2px(this, 19);
            } else if (mCartFoodSize < 99) {
                mCateNumTv.setBackgroundResource(R.drawable.bg_corner_cart_num_ff0000);
                params.width = ExtendUtils.getInstance().dip2px(this, 25);
                params.height = ExtendUtils.getInstance().dip2px(this, 15);
            } else {
                mCateNumTv.setBackgroundResource(R.drawable.bg_corner_cart_num_ff0000);
                params.width = ExtendUtils.getInstance().dip2px(this, 25);
                params.height = ExtendUtils.getInstance().dip2px(this, 15);
            }

            mCateNumTv.setLayoutParams(params);
        }
    }

    private MainBottomLayout.OnOperateListener mBottomViewListener = new MainBottomLayout.OnOperateListener() {
        @Override
        public void onMenuSelected(int selectedId) {
            if (selectedId == R.id.rb_cart) {
                return;
            }

            if (selectedId != R.id.rb_home &&
                    StringUtils.getInstance().isNullOrEmpty(Utils.getToken(MainActivity.this))) {
                // 未登录
                LoginActivity.invoke(MainActivity.this, false);
                mBottomLayout.setHomeChecked();
                return;
            }

            if (null != mAdapter) {
                mAdapter.switchFragment(selectedId);
            }
        }

    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        mBottomLayout = (MainBottomLayout) this.findViewById(R.id.layout_bottom);
        mBottomLayout.setListener(mBottomViewListener);

        initFragmentPage();

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
                            CartActivity.invoke(MainActivity.this);
                        }
                        break;
                    }
                }
                return true;
            }
        });

        mBottomCartIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartActivity.invoke(MainActivity.this);
            }
        });
    }

    private void moveViewWithFinger(View view, float rawX, float rawY) {
        int minY = ExtendUtils.getInstance().dip2px(MainActivity.this, 50) + mCategoryLayout.getHeight() / 2;
        int maxY = AppConfig.getScreenHeight() - minY;

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
            params.topMargin = (int) rawY - ExtendUtils.getInstance().dip2px(MainActivity.this, 50);
        } else {
            params.topMargin = (int) rawY - minY;
        }

        view.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CartManager.getInstance().getAllFoods(mHandler);
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        EventBus.getDefault().register(this);

        if (StringUtils.getInstance().isNullOrEmpty(Utils.getToken(MainActivity.this))) {
            // 没有登录
            showLoginTipDialog();
        }
    }

    private void showLoginTipDialog() {
        PromptDialogBuilder builder = new PromptDialogBuilder(this);
        builder.setIsHasBackground(false);

        View view = LayoutInflater.from(this).inflate(R.layout.layout_login_prompt, null);
        LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.layout_login_content);

        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(AppConfig.getScreenWidth() -
                        2 * ExtendUtils.getInstance().dip2px(MainActivity.this, 20),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        contentLayout.setLayoutParams(contentParams);

        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLoginTipDialog) {
                    mLoginTipDialog.dismiss();
                }

                LoginActivity.invoke(MainActivity.this, false);
            }
        });
        builder.setView(view);
        mLoginTipDialog = builder.create();
        mLoginTipDialog.show();
    }

    /**
     * 初始化页面
     */
    private void initFragmentPage() {
        mFragments.clear();

        mHomeFragment = new HomeFragment();
        mWalletFragment = new WalletFragment();
        mFavoriteFragment = new FavoriteFragment();
        mOrderFragment = new OrderFragment();
        mPersonalFragment = new PersonalFragment();

        mFragments.add(mHomeFragment);
        mFragments.add(mWalletFragment);
//        mFragments.add(mFavoriteFragment);
        mFragments.add(null);
        mFragments.add(mOrderFragment);
        mFragments.add(mPersonalFragment);

        mAdapter = new MainPageAdapter(MainActivity.this, mFragments,
                R.id.main_detail_fragment, mBottomLayout);

        // 默认选中主页
        mBottomLayout.setHomeChecked();
    }

    @Override
    public void onBackPressed() {
        long currentMills = System.currentTimeMillis();
        if (currentMills - mLastBackPressTime <= MAX_BACK_DURATION) {
            this.finish();
            WaimaiApplication.getInstance().exit();
        } else {
            PromptUtils.getInstance().showLongPromptToast(this, R.string.exit_after_press_again);
            mLastBackPressTime = currentMills;
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
            case EventID.CHANGE_LANGUAGE_SUCCESS: {
                MainActivity.this.finish();
                break;
            }

            case EventID.LOGOUT_SUCCESS: {
                // 退出登录
                MainActivity.this.finish();
                break;
            }
        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
