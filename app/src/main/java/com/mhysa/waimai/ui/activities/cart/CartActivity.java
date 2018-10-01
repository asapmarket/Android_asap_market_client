package com.mhysa.waimai.ui.activities.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
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
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.order.AmountDes;
import com.mhysa.waimai.model.order.CreateOrder;
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.order.CreateOrderActivity;
import com.mhysa.waimai.ui.customerviews.cart.CartStoreItemLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 购物车
 * Date: 2017/8/5
 *
 * @author xusheng
 */

public class CartActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_cart)
    LinearLayout mCartLayout;

    @Bind(R.id.tv_price)
    TextView mPriceTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CartManager.GET_ALL_FOOD_SORT_BY_STORE_SUCCESS: {
                    closeNetDialog();

                    try {
                        HashMap<String, List<CartFood>> cartFoodHashMap = (HashMap<String, List<CartFood>>) msg.obj;
                        if (null != cartFoodHashMap && cartFoodHashMap.size() > 0) {
                            assembleData(cartFoodHashMap);
                        } else {
                            mCartLayout.removeAllViews();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    changePrice();

                    break;
                }

                case CartManager.CLEAR_CART_SUCCESS: {
                    closeNetDialog();
                    mCartLayout.removeAllViews();
                    CartActivity.this.finish();
                    break;
                }

                default: {
                    break;
                }
            }
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_cart;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.cart_name);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * 重新组装数据，适配界面
     *
     * @param cartFoodHashMap
     */
    private void assembleData(HashMap<String, List<CartFood>> cartFoodHashMap) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ExtendUtils.getInstance().dip2px(CartActivity.this, 10);

        mCartLayout.removeAllViews();
        Iterator iterator = cartFoodHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            List<CartFood> cartFoods = (List<CartFood>) entry.getValue();

            if (null != cartFoods
                    && cartFoods.size() > 0) {
                CartStoreItemLayout layout = new CartStoreItemLayout(this);
                layout.setData((String) entry.getKey(), cartFoods);
                layout.setListener(new CartStoreItemLayout.CartStoreItemListener() {
                    @Override
                    public void OnStatusChanged() {
                        // 改变价格
                        changePrice();
                    }

                    @Override
                    public void OnDelete() {
                        getAllCartDatas();
                    }

                    @Override
                    public void onNumChanged() {
                        // 改变价格
                        changePrice();
                    }
                });
                mCartLayout.addView(layout, params);
            }
        }

        closeNetDialog();
    }

    private void changePrice() {
        BigDecimal price = new BigDecimal(0.0);

        if (null != mCartLayout && mCartLayout.getChildCount() > 0) {
            final int count = mCartLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                BigDecimal childPrice = ((CartStoreItemLayout) mCartLayout.getChildAt(i)).getTotalPrice();

                if (null != childPrice) {
                    price = price.add(childPrice);
                }
            }
        }

        if (null != price) {
            mPriceTv.setText("$" + price.toPlainString() + "");
        }
    }

    /**
     * 从数据库取出所有的购物车信息
     */
    private void getAllCartDatas() {
        CartManager.getInstance().getAllFoodsSortByStore(mHandler);
    }

    @OnClick({R.id.layout_back, R.id.tv_clear_category,
            R.id.btn_account})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                CartActivity.this.finish();
                break;
            }

            case R.id.tv_clear_category: {
                AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(CartActivity.this, true);
                builder.setMessage(R.string.clear_cart_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showNetDialog("");
                                CartManager.getInstance().clearCartTable(mHandler);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            }

            case R.id.btn_account: {
                // 结算
                if (null != mCartLayout && mCartLayout.getChildCount() > 0) {
                    final int count = mCartLayout.getChildCount();
                    HashMap<String, List<CartFood>> foodsMap = new HashMap<String, List<CartFood>>();
                    for (int i = 0; i < count; i++) {
                        CartStoreItemLayout layout = (CartStoreItemLayout) mCartLayout.getChildAt(i);
                        if (null != layout) {
                            List<CartFood> cartFoods = layout.getSelectedFoods();
                            if (null != cartFoods && cartFoods.size() > 0) {
                                foodsMap.put(cartFoods.get(0).store_id, cartFoods);
                            }
                        }
                    }

                    if (null != foodsMap && foodsMap.size() > 0) {
                        if (StringUtils.getInstance().isNullOrEmpty(Utils.getToken(CartActivity.this))) {
                            LoginActivity.invoke(CartActivity.this, false);
                        } else {
                            // 提交结算，首先调用接口判断是否有打烊商家
                            countAmount(foodsMap);
                        }
                    } else {
                        PromptUtils.getInstance().showShortPromptToast(CartActivity.this, R.string.no_food_selected);
                    }
                }
                break;
            }

            default: {
                break;
            }
        }
    }

    private void countAmount(final HashMap<String, List<CartFood>> foodsMap) {
        CreateOrder createOrder = new CreateOrder();
        createOrder.user_id = Utils.getUserId(this);
        createOrder.token = Utils.getToken(this);

        Order order = new Order();

        List<Store> stores = new ArrayList<Store>();
        if (null != foodsMap && foodsMap.size() > 0) {
            Iterator<Map.Entry<String, List<CartFood>>> it = foodsMap.entrySet().iterator();
            while (it.hasNext()) {
                Store store = new Store();

                Map.Entry<String, List<CartFood>> entry = it.next();
                store.store_id = entry.getKey();

                List<Food> foods = new ArrayList<Food>();
                List<CartFood> cartFoods = (List<CartFood>) entry.getValue();
                if (null != cartFoods && cartFoods.size() > 0) {
                    final int size = cartFoods.size();
                    for (int i = 0; i < size; i++) {
                        CartFood cartFood = cartFoods.get(i);
                        Food food = new Food();
                        food.foods_id = cartFood.foods_id;

                        // 升序排列
                        String spec_id_list = cartFood.spec_id_list;
                        if (!StringUtils.getInstance().isNullOrEmpty(spec_id_list)) {
                            String[] specialIds = spec_id_list.split(",");
                            if (null == specialIds
                                    || specialIds.length == 0) {
                                return;
                            }

                            final int length = specialIds.length;
                            int a[] = new int[length];
                            for (int j = 0; j < length; j++) {
                                a[j] = Integer.valueOf(specialIds[j]);
                            }

                            int temp = 0;
                            for (int k = length - 1; k > 0; --k) {
                                for (int j = 0; j < k; ++j) {
                                    if (a[j + 1] < a[j]) {
                                        temp = a[j];
                                        a[j] = a[j + 1];
                                        a[j + 1] = temp;
                                    }
                                }
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (int j = 0; j < length; j++) {
                                stringBuilder.append(a[j]);

                                if (j != length - 1) {
                                    stringBuilder.append(",");
                                }
                            }

                            food.spec_id_list = stringBuilder.toString();
                        }

                        food.foods_quantity = cartFood.quality;
                        food.quantity = cartFood.quality;
                        food.price = cartFood.price;

                        foods.add(food);
                    }
                }

                store.foods_list = foods;
                stores.add(store);
            }
        }

        order.store_list = stores;

        createOrder.order = order;

        showNetDialog("");
        OkHttpClientManager.getInstance().countAmount(createOrder, new WtNetWorkListener<AmountDes>() {
            @Override
            public void onSucess(final RemoteReturnData<AmountDes> data) {
                if (null != data && null != data.data && null != data.data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != data.data.store_ids && data.data.store_ids.length > 0) {
                                // 商家打烊
//                                EventBus.getDefault().post(new ApplicationEvent(EventID.DO_HAVE_CLOSED_STORE, data.data.store_ids));
                                showStoreCloseTip(data.data.store_name_cn, data.data.store_name_en);
                            } else {
                                CreateOrderActivity.invoke(CartActivity.this, foodsMap, data.data);
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeNetDialog();
                        }
                    });
                }
            }

            @Override
            public void onError(final String status, final String msg_cn, final String msg_en) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseError(msg_cn, msg_en);
                        closeNetDialog();
                        LoginActivity.loginError(CartActivity.this, status);
                    }
                });
            }

            @Override
            public void onFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeNetDialog();
                    }
                });
            }
        });
    }

    private void showStoreCloseTip(String storeNameCn, String storeNameEn) {
        if (Utils.isChinese(this)
                && StringUtils.getInstance().isNullOrEmpty(storeNameCn)) {
            return;
        }

        if (!Utils.isChinese(this)
                && StringUtils.getInstance().isNullOrEmpty(storeNameEn)) {
            return;
        }
        String storeName = Utils.isChinese(this) ? storeNameCn : storeNameEn;

        AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(this, true);
        builder.setMessage(getString(R.string.has_closed_store, storeName))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);

        showNetDialog("");
        getAllCartDatas();
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
                CartActivity.this.finish();
                break;
            }

            case EventID.DO_HAVE_CLOSED_STORE: {
//                if (null != event.getData()) {
//                    String[] storeIds = (String[]) event.getData();
//                    if (null != storeIds && storeIds.length > 0) {
//                        final int length = storeIds.length;
//                        for (int i = 0; i < length; i++) {
//                            String storeId = storeIds[i];
//                            CartManager.getInstance().deleteByStoreId(storeId);
//                            removeFromLayout(storeId);
//                        }
//
//                        changePrice();
//                    }
//                }
                break;
            }
        }
    }

    private void removeFromLayout(String storeId) {
        if (StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }

        boolean hasFound = false;
        final int count = mCartLayout.getChildCount();
        for (int i = 0; i < count && !hasFound; i++) {
            CartStoreItemLayout layout = (CartStoreItemLayout) mCartLayout.getChildAt(i);
            if (null != layout && !StringUtils.getInstance().isNullOrEmpty(layout.getStoreId())) {
                if (layout.getStoreId().equals(storeId)) {
                    mCartLayout.removeViewAt(i);
                }
            }
        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, CartActivity.class);
        context.startActivity(intent);
    }
}
