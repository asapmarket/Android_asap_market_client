package com.mhysa.waimai.ui.activities.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.joey.devilfish.fusion.FusionCode;
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
import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.model.card.Card;
import com.mhysa.waimai.model.cart.CartFood;
import com.mhysa.waimai.model.coupon.Coupon;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.order.AmountDes;
import com.mhysa.waimai.model.order.CreateOrder;
import com.mhysa.waimai.model.order.DistributionTime;
import com.mhysa.waimai.model.order.DistributionTimeList;
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.model.order.PaypalNotify;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.activities.base.BasePayActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.personal.AddressListActivity;
import com.mhysa.waimai.ui.activities.personal.CardListActivity;
import com.mhysa.waimai.ui.customerviews.order.CreateOrderStoreItemLayout;
import com.mhysa.waimai.ui.customerviews.order.PaymethodChooseLayout;
import com.mhysa.waimai.ui.fragments.bottommenu.DistributionTimeBottomFragment;
import com.mhysa.waimai.ui.fragments.bottommenu.InputCouponCodeFragment;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.model.Token;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/8/7
 *
 * @author xusheng
 */

public class CreateOrderActivity extends BasePayActivity {

    private static final String INTENT_CART_FOOD = "intent_cart_food";

    private static final String INTENT_AMOUNT_DES = "intent_amount_des";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.tv_address)
    TextView mAddressTv;

    @Bind(R.id.layout_selected_address)
    RelativeLayout mSelectedAddressLayout;

    @Bind(R.id.tv_selected_address)
    TextView mSelectedAddressTv;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.tv_sex)
    TextView mSexTv;

    @Bind(R.id.tv_phone)
    TextView mPhoneTv;

    @Bind(R.id.layout_foods)
    LinearLayout mFoodsLayout;

    @Bind(R.id.et_remark)
    EditText mRemarkEt;

    @Bind(R.id.tv_payment)
    TextView mPaymentTv;

    @Bind(R.id.tv_tax_description)
    TextView mTaxDesTv;

    @Bind(R.id.btn_commit)
    Button mCommitBtn;

    @Bind(R.id.layout_distribution_time)
    RelativeLayout mDistributionTimeLayout;

    @Bind(R.id.tv_distribution_time)
    TextView mDistributionTimeTv;

    @Bind(R.id.tv_without_coupon)
    TextView mWithoutCouponTv;

    @Bind(R.id.tv_coupon_cut)
    TextView mCouponCutTv;

    @Bind(R.id.tv_delivery_fee)
    TextView mDeliveryFeeTv;

    @Bind(R.id.tv_tax)
    TextView mTaxTv;

    @Bind(R.id.tv_coupon_cut_pre)
    TextView mCouponCutPreTv;

    @Bind(R.id.tv_total_price)
    TextView mTotalPriceTv;

    private Address mAddress;

    private HashMap<String, List<CartFood>> mCartFoodsMap
            = new HashMap<String, List<CartFood>>();

    private Dialog mTipDialog;

    private Handler mHandler = new Handler();

    private String mOrderId;

    private Order mOrder;

    private AmountDes mAmountDes;

    private Coupon mCoupon;

    private List<DistributionTime> mDistributionTimes = new ArrayList<DistributionTime>();

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mCartFoodsMap = (HashMap<String, List<CartFood>>) getIntent().getSerializableExtra(INTENT_CART_FOOD);
            mAmountDes = (AmountDes) getIntent().getSerializableExtra(INTENT_AMOUNT_DES);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_create_order;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.create_order);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        EventBus.getDefault().register(this);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        if (null != mCartFoodsMap
                && mCartFoodsMap.size() > 0) {
            mFoodsLayout.removeAllViews();
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = ExtendUtils.getInstance().dip2px(CreateOrderActivity.this, 10);

            Iterator iterator = mCartFoodsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                List<CartFood> cartFoods = (List<CartFood>) entry.getValue();
                CreateOrderStoreItemLayout layout = new CreateOrderStoreItemLayout(CreateOrderActivity.this);
                BigDecimal bigDecimal = layout.setData(cartFoods);
                mFoodsLayout.addView(layout, params);
            }
        }

        drawAmount();
    }

    private void drawAmount() {
        if (null == mAmountDes) {
            return;
        }

        mRewardPoint = mAmountDes.point_balance;

        if (null != mAmountDes.default_address) {
            mAddress = mAmountDes.default_address;
            drawAddress(mAddress);
        }

        drawPrice("", mAmountDes.yunfei, mAmountDes.tax, mAmountDes.total_money);
    }

    private void drawPrice(String couponMoney, String yunfei, String tax, String totalMoney) {
        if (!StringUtils.getInstance().isNullOrEmpty(couponMoney)) {
            mCouponCutPreTv.setVisibility(View.VISIBLE);
            mCouponCutPreTv.setText(getString(R.string.coupon_cut_pre, couponMoney));
        } else {
            mCouponCutPreTv.setVisibility(View.GONE);
        }

        if (!StringUtils.getInstance().isNullOrEmpty(yunfei)) {
            mDeliveryFeeTv.setText(getString(R.string.delivery_fee, yunfei));
        }

        if (!StringUtils.getInstance().isNullOrEmpty(tax)) {
            mTaxTv.setText(getString(R.string.tax, tax));
        }

        if (!StringUtils.getInstance().isNullOrEmpty(totalMoney)) {
            mTotalPriceTv.setText("$" + totalMoney);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        if (Utils.isChinese(this)) {
            mTaxDesTv.setText("首个餐厅只收取20%订单总价的送餐费\n" +
                    "每增加一个餐厅订单总价增加$2.0");
        } else {
            mTaxDesTv.setText("We Charge 20% delivery fee from subtatall.\n" +
                    "You can add dishes from mutiple different restaurants," +
                    " a $2.0 processing fee will be added for each more restuarant.");
        }
    }

    @OnClick({R.id.layout_back, R.id.layout_address,
            R.id.layout_payment, R.id.btn_commit,
            R.id.layout_distribution_time, R.id.layout_coupon})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                CreateOrderActivity.this.finish();
                break;
            }

            case R.id.layout_address: {
                AddressListActivity.invoke(CreateOrderActivity.this, true);
                break;
            }

            case R.id.layout_payment: {
                choosePayments(false);
                break;
            }

            case R.id.btn_commit: {
                commit();
                break;
            }

            case R.id.layout_distribution_time: {
                if (null != mDistributionTimes && mDistributionTimes.size() > 0) {
                    showMenu();
                }
                break;
            }

            case R.id.layout_coupon: {
                // 优惠券
                InputCouponCodeFragment inputCouponCodeFragment = new InputCouponCodeFragment();
                inputCouponCodeFragment.setListener(new InputCouponCodeFragment.OnFeedbackListener() {
                    @Override
                    public void onConfirm(String result) {
                        try {
                            // 根据优惠码获取优惠金额
                            Coupon coupon = new Coupon();
                            coupon.user_id = Utils.getUserId(CreateOrderActivity.this);
                            coupon.token = Utils.getToken(CreateOrderActivity.this);
                            coupon.COUPON_NUM = result;
                            if (null != mAmountDes) {
                                coupon.yunfei = mAmountDes.yunfei;
                                coupon.tax = mAmountDes.tax;
                                coupon.total_money = mAmountDes.total_money;
                            }

                            OkHttpClientManager.getInstance().userCoupon(coupon, new WtNetWorkListener<Coupon>() {
                                @Override
                                public void onSucess(final RemoteReturnData<Coupon> data) {
                                    if (null != data && null != data.data) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                drawPrice(data.data.COUPON_MONEY, data.data.yunfei, data.data.tax, data.data.total_money);

                                                if (!StringUtils.getInstance().isNullOrEmpty(data.data.COUPON_MONEY)) {
                                                    mWithoutCouponTv.setVisibility(View.GONE);
                                                    mCouponCutTv.setVisibility(View.VISIBLE);
                                                    mCouponCutTv.setText(getString(R.string.coupon_cut, data.data.COUPON_MONEY));
                                                }

                                                mCoupon = data.data;
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(String status, final String msg_cn, final String msg_en) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            responseError(msg_cn, msg_en);
                                        }
                                    });
                                }

                                @Override
                                public void onFinished() {

                                }
                            });
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                inputCouponCodeFragment.show(getFragmentManager(), "InputCouponCodeFragment");
                break;
            }

            default: {
                break;
            }
        }
    }

    private void showMenu() {
        DistributionTimeBottomFragment fragment = new DistributionTimeBottomFragment();
        fragment.setData(mDistributionTimes);
        fragment.setListener(new DistributionTimeBottomFragment.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(DistributionTime time) {
                mDistributionTimeTv.setText(time.distribution_time);
            }
        });
        fragment.show(getFragmentManager(), "DistributionTimeBottomFragment");
    }

    private void commit() {
        if (mIsInCommiting) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.paying);
            return;
        }
        if (null == mAddress ||
                StringUtils.getInstance().isNullOrEmpty(mAddress.extm_id)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.choose_address_hint);
            return;
        }

        if (StringUtils.getInstance().isNullOrEmpty(mPayMethod)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.choose_payment_hint);
            return;
        }

        if (null != mAmountDes && mAmountDes.money_limit.equals(FusionCode.MoneyLimit.MONEY_LIMIT_NO)) {
            // 提示用户金额不满足
            showTipDialog();
            return;
        }

        doCommit();
    }

    private void showTipDialog() {
        if (null != mTipDialog
                && mTipDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(this, true);

        builder.setTitle(R.string.tip);
        if (Utils.isChinese(this)) {
            builder.setMessage(mAmountDes.count_words_cn);
        } else {
            builder.setMessage(mAmountDes.count_words_en);
        }

        builder.setPositiveButton(R.string.continue_pay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTipDialog.dismiss();
                mTipDialog = null;
                doCommit();
            }
        });
        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTipDialog.dismiss();
                mTipDialog = null;
                CreateOrderActivity.this.finish();
            }
        });

        mTipDialog = builder.create();
        mTipDialog.show();
    }

    private void doCommit() {
        commitButtonEnabled(false);

        CreateOrder createOrder = new CreateOrder();
        createOrder.user_id = Utils.getUserId(this);
        createOrder.token = Utils.getToken(this);

        Order order = new Order();
        order.payment_method = mPayMethod;
        order.remark = mRemarkEt.getText().toString().trim();
        order.extm_id = mAddress.extm_id;

        String distributionTime = mDistributionTimeTv.getText().toString().trim();
        order.distribution_time = StringUtils.getInstance().isNullOrEmpty(distributionTime) ? "" : distributionTime;

        List<Store> stores = new ArrayList<Store>();
        if (null != mCartFoodsMap && mCartFoodsMap.size() > 0) {
            Iterator<Map.Entry<String, List<CartFood>>> it = mCartFoodsMap.entrySet().iterator();
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

        if (null != mCoupon) {
            createOrder.COUPON_MONEY = mCoupon.COUPON_MONEY;
            createOrder.COUPON_NUM = mCoupon.COUPON_NUM;
        }

        showNetDialog("");
        OkHttpClientManager.getInstance().createOrder(createOrder, new WtNetWorkListener<CreateOrder>() {
            @Override
            public void onSucess(final RemoteReturnData<CreateOrder> data) {
                if (null != data && null != data.data) {
                    if (null != data.data.store_ids && data.data.store_ids.length > 0) {
                        // 商家打烊
//                        PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.has_closed_store);
//                        EventBus.getDefault().post(new ApplicationEvent(EventID.DO_HAVE_CLOSED_STORE, data.data.store_ids));
                        showStoreCloseTip(data.data.store_name_cn, data.data.store_name_en);
                        CreateOrderActivity.this.finish();
                    } else {
                        if (null != data.data.order) {
                            // 成功
                            if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_CASH) ||
                                    mPayMethod.equals(FusionCode.PayMethod.PAY_REWARD_POINT)) {
                                // 现金支付
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.pay_success);
                                        EventBus.getDefault().post(new ApplicationEvent(EventID.PAY_SUCCESS));
                                        OrderDetailActivity.invoke(CreateOrderActivity.this, data.data.order.order_id);
                                    }
                                });
                            } else {
                                doPay(data.data.order);
                            }
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commitButtonEnabled(true);
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
                        commitButtonEnabled(true);
                        responseError(msg_cn, msg_en);
                        closeNetDialog();
                        LoginActivity.loginError(CreateOrderActivity.this, status);
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

    private void doPay(Order order) {

        if (null == order
                || StringUtils.getInstance().isNullOrEmpty(order.order_id)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commitButtonEnabled(true);
                    closeNetDialog();
                }
            });
            return;
        }

        mOrderId = order.order_id;

        if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_PAYPAL)) {
            doPaypalPay(order);
        } else if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_CREDIT_CARD)) {
            doCreditCardPay(order);
        }
    }

    private void doCreditCardPay(Order order) {
        if (null == order
                || StringUtils.getInstance().isNullOrEmpty(order.total_money)
                || StringUtils.getInstance().isNullOrEmpty(order.order_id)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commitButtonEnabled(true);
                }
            });
            return;
        }

        this.mOrder = order;
//        Intent intent = new Intent(CreateOrderActivity.this, CreditCardSettingActivity.class);
//        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE);
        Intent intent = new Intent(CreateOrderActivity.this, CardListActivity.class);
        intent.putExtra(CardListActivity.INTENT_IS_CHOOSE, true);
        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE);
    }

    public void doPaypalPay(Order order) {
        if (null == order
                || StringUtils.getInstance().isNullOrEmpty(order.total_money)
                || StringUtils.getInstance().isNullOrEmpty(order.order_id)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    commitButtonEnabled(true);
                }
            });
            return;
        }

        //创建支付对象，用于传过去给PayPal服务器进行收款
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, order.total_money);
        Intent intent = new Intent(CreateOrderActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        //这里直接调起PayPal的sdk进行付款操作
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    //这里只传一个总价格或者单个产品的信息收款情况
    private PayPalPayment getThingToBuy(String paymentIntent, String money) {
        return new PayPalPayment(new BigDecimal(money), "USD", "Total", paymentIntent);
    }

    /**
     * 删除购物车
     */
    private void deleteCart() {
        if (null != mCartFoodsMap && mCartFoodsMap.size() > 0) {
            try {
                Iterator<Map.Entry<String, List<CartFood>>> it = mCartFoodsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, List<CartFood>> entry = it.next();
                    String storeId = entry.getKey();

                    List<CartFood> cartFoods = (List<CartFood>) entry.getValue();
                    if (null != cartFoods && cartFoods.size() > 0) {
                        final int size = cartFoods.size();
                        for (int i = 0; i < size; i++) {
                            CartFood cartFood = cartFoods.get(i);
                            CartManager.getInstance().deleteAllTargetFood(mHandler, cartFood.foods_id,
                                    storeId, cartFood.spec_id_list);
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void drawAddress(Address address) {
        if (null != address) {
            mAddressTv.setVisibility(View.GONE);
            mSelectedAddressLayout.setVisibility(View.VISIBLE);
            mAddress = address;
            StringUtils.getInstance().setText(mAddress.address, mSelectedAddressTv);
            StringUtils.getInstance().setText(mAddress.extm_name, mNameTv);
            StringUtils.getInstance().setText(mAddress.extm_phone, mPhoneTv);

            if (!StringUtils.getInstance().isNullOrEmpty(address.sex)
                    && address.sex.equals(FusionCode.Sex.FEMALE)) {
                mSexTv.setText(R.string.female);
            } else {
                mSexTv.setText(R.string.male);
            }

            if (!StringUtils.getInstance().isNullOrEmpty(address.zip_code)) {
                getDistributionTimeListByZipCode(address.zip_code);
            }
        }
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        if (null == event) {
            return;
        }

        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.CHOOSE_ADDRESS_SUCCESS: {
                if (null != event.getData()) {
                    Address address = (Address) event.getData();
                    drawAddress(address);
                }
                break;
            }

            case EventID.PAY_SUCCESS: {
                // 支付成功
                // 删除购物车
                deleteCart();
                CreateOrderActivity.this.finish();
                break;
            }

            case EventID.ADDRESS_IS_EMPTY: {
                mAddressTv.setVisibility(View.VISIBLE);
                mSelectedAddressLayout.setVisibility(View.GONE);
                mAddress = null;
                StringUtils.getInstance().setText("", mSelectedAddressTv);
                StringUtils.getInstance().setText("", mNameTv);
                StringUtils.getInstance().setText("", mPhoneTv);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(CreateOrderActivity.this, PayPalService.class));
    }

    public PaymethodChooseLayout.OnPaymethodChooseListener getPaymethodListener() {
        return mPaymethodListener;
    }

    private PaymethodChooseLayout.OnPaymethodChooseListener mPaymethodListener = new PaymethodChooseLayout.OnPaymethodChooseListener() {
        @Override
        public void onSelected(String paymethod) {
            if (null != mChoosePaymethodDialog) {
                mChoosePaymethodDialog.dismiss();
                mChoosePaymethodDialog = null;
            }

            if (!StringUtils.getInstance().isNullOrEmpty(paymethod)) {
                mPayMethod = paymethod;
                if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_VISA)) {
                    mPaymentTv.setText(getResources().getString(R.string.paymethod_visa));
                } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_PAYPAL)) {
                    mPaymentTv.setText(getResources().getString(R.string.paymethod_paypal));
                } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_BALANCE)) {
                    mPaymentTv.setText(getResources().getString(R.string.paymethod_balance1));
                } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_CREDIT_CARD)) {
                    mPaymentTv.setText(getResources().getString(R.string.paymethod_credit_card));
                } else if (paymethod.equals(FusionCode.PayMethod.PAY_METHOD_CASH)) {
                    mPaymentTv.setText(R.string.pay_in_cash);
                } else if (paymethod.equals(FusionCode.PayMethod.PAY_REWARD_POINT)) {
                    mPaymentTv.setText(R.string.paymethod_rewardpoint);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        payPalNotify(confirm.toJSONObject().toString(4));
                    } catch (JSONException e) {
                        payPalNotify("");
                    }
                }
            } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
                // 信用卡支付
                Card card = (Card) data.getSerializableExtra("creditCard");
                if (null != card) {
                    createToken(card);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                // 交易取消
                payPalNotify("");
            } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
                // 信用卡支付
                commitButtonEnabled(true);
                PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.pay_cancel);
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                payPalNotify("");
            } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
                // 信用卡支付
                commitButtonEnabled(true);
                PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.pay_cancel);
            }
        }
    }

    private void payPalNotify(String notify) {
        PaypalNotify paypalNotify = new PaypalNotify();
        if (!StringUtils.getInstance().isNullOrEmpty(notify)) {
            paypalNotify.notify = notify;
        }
        paypalNotify.order_id = mOrderId;

        OkHttpClientManager.getInstance().paypalNotify(paypalNotify, new WtNetWorkListener<JsonElement>() {
            @Override
            public void onSucess(RemoteReturnData<JsonElement> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.pay_success);
                        EventBus.getDefault().post(new ApplicationEvent(EventID.PAY_SUCCESS));
                        OrderDetailActivity.invoke(CreateOrderActivity.this, mOrderId);
                    }
                });

            }

            @Override
            public void onError(final String status, final String msg_cn, final String msg_en) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(CreateOrderActivity.this, status);
                    }
                });

            }

            @Override
            public void onFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commitButtonEnabled(true);
                    }
                });
            }
        });
    }

    private boolean mIsInCommiting = false;

    private void commitButtonEnabled(boolean isEnabled) {
        mIsInCommiting = !isEnabled;

//        mCommitBtn.setEnabled(isEnabled);
//
//        if (isEnabled) {
//            mCommitBtn.setBackgroundColor(Color.parseColor("#2196f3"));
//        } else {
//            mCommitBtn.setBackgroundColor(Color.parseColor("#bdbdbd"));
//        }
    }

    private void getDistributionTimeListByZipCode(String zipCode) {
        mDistributionTimeTv.setText("");
        mDistributionTimes.clear();
        OkHttpClientManager.getInstance().getDistributionTimeListByZipCode(zipCode, new WtNetWorkListener<DistributionTimeList>() {
            @Override
            public void onSucess(RemoteReturnData<DistributionTimeList> data) {
                if (null != data
                        && null != data.data
                        && null != data.data.rows) {
                    mDistributionTimeLayout.setVisibility(View.VISIBLE);
                    mDistributionTimes.clear();
                    mDistributionTimes.addAll(data.data.rows);
                    try {
                        mDistributionTimeTv.setText(mDistributionTimes.get(0).distribution_time);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                } else {
                    mDistributionTimeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {
                responseError(msg_cn, msg_en);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void createStripeTokenSuccess(Token token) {
        showNetDialog("");
        OkHttpClientManager.getInstance().creditCardPay(mOrder.order_id,
                token.getId(), "0", new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commitButtonEnabled(true);
                                PromptUtils.getInstance().showShortPromptToast(CreateOrderActivity.this, R.string.pay_success);
                                EventBus.getDefault().post(new ApplicationEvent(EventID.PAY_SUCCESS));
                                OrderDetailActivity.invoke(CreateOrderActivity.this, mOrderId);
                            }
                        });

                    }

                    @Override
                    public void onError(String status, final String msg_cn, final String msg_en) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commitButtonEnabled(true);
                                responseError(msg_cn, msg_en);
                            }
                        });
                    }

                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commitButtonEnabled(true);
                                closeNetDialog();
                            }
                        });
                    }
                });
    }

    @Override
    public void createStripeTokenFail() {
        commitButtonEnabled(true);
    }

    public static void invoke(Context context,
                              HashMap<String, List<CartFood>> cartFoods, AmountDes amountDes) {
        Intent intent = new Intent(context, CreateOrderActivity.class);
        intent.putExtra(INTENT_CART_FOOD, cartFoods);
        intent.putExtra(INTENT_AMOUNT_DES, amountDes);
        context.startActivity(intent);
    }
}
