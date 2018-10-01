package com.mhysa.waimai.ui.activities.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.card.Card;
import com.mhysa.waimai.model.errand.ErrandOrder;
import com.mhysa.waimai.model.errand.ErrandRewardPoint;
import com.mhysa.waimai.model.order.PaypalNotify;
import com.mhysa.waimai.model.user.ExpLngLat;
import com.mhysa.waimai.ui.activities.base.BasePayActivity;
import com.mhysa.waimai.ui.activities.errand.ErrandHomeActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.map.MapActivity;
import com.mhysa.waimai.ui.activities.personal.CardListActivity;
import com.mhysa.waimai.ui.customerviews.addpicture.AddPictureLayout;
import com.mhysa.waimai.ui.customerviews.order.ErrandOrderScheduleLayout;
import com.mhysa.waimai.ui.customerviews.order.OrderDetailMapLayout;
import com.mhysa.waimai.ui.customerviews.order.PaymethodChooseLayout;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.model.Token;

import org.json.JSONException;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2018/3/15
 *
 * @author xusheng
 */

public class ErrandOrderDetailActivity extends BasePayActivity implements OnMapReadyCallback {

    private static final String INTENT_ORDER_ID = "intent_order_id";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.scrollView)
    PullToRefreshScrollView mScrollView;

    @Bind(R.id.tv_order_id)
    TextView mOrderIdTv;

    @Bind(R.id.layout_schedule)
    ErrandOrderScheduleLayout mScheduleLayout;

    @Bind(R.id.iv_arrow)
    ImageView mArrowIv;

    @Bind(R.id.layout_map)
    OrderDetailMapLayout mMapLayout;

    @Bind(R.id.layout_map_layer)
    RelativeLayout mMapLayerLayout;

    @Bind(R.id.tv_total_price)
    TextView mTotalPriceTv;

    @Bind(R.id.tv_price_desc)
    TextView mPriceDescTv;

    @Bind(R.id.tv_order_time)
    TextView mOrderTimeTv;

    @Bind(R.id.tv_cust_name)
    TextView mCustNameTv;

    @Bind(R.id.tv_cust_phone)
    TextView mCustPhoneTv;

    @Bind(R.id.tv_cust_address)
    TextView mCustAddressTv;

    @Bind(R.id.tv_remark)
    TextView mRemarkTv;

    @Bind(R.id.layout_images)
    AddPictureLayout mPictureLayout;

    @Bind(R.id.tv_not_feedback_tip)
    TextView mNotFeedbackTipTv;

    @Bind(R.id.layout_price)
    LinearLayout mPriceLayout;

    @Bind(R.id.layout_btns)
    LinearLayout mBtnLayout;

    @Bind(R.id.btn_cancel)
    Button mCancelBtn;

    @Bind(R.id.btn_pay)
    Button mPayBtn;

    private String mOrderId;

    private ErrandOrder mErrandOrder;

    private String mExpId;

    private GoogleMap mMap = null;

    private boolean mInPay = false;

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mOrderId = getIntent().getStringExtra(INTENT_ORDER_ID);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_errand_order_detail;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.order_detail);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // 刷新
                getOrderDetail();
            }
        });

        mMapLayout.setScrollView(mScrollView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        showNetDialog("");
        getOrderDetail();
        getRewardPoint();
    }

    private void getRewardPoint() {
        OkHttpClientManager.getInstance().getErrandRewardPoint(Utils.getToken(this), Utils.getUserId(this),
                new WtNetWorkListener<ErrandRewardPoint>() {
                    @Override
                    public void onSucess(final RemoteReturnData<ErrandRewardPoint> data) {
                        if (null != data && null != data.data) {
                            mRewardPoint = data.data.user_point;
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
    }

    private void getOrderDetail() {
        OkHttpClientManager.getInstance().getErrandOrderDetail(Utils.getUserId(this),
                Utils.getToken(this), mOrderId, new WtNetWorkListener<ErrandOrder>() {
                    @Override
                    public void onSucess(final RemoteReturnData<ErrandOrder> data) {
                        if (null != data && null != data.data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawViews(data.data);
                                    mExpId = data.data.exp_id;
                                    getExpLocation();
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
                                LoginActivity.loginError(ErrandOrderDetailActivity.this, status);
                            }
                        });
                    }

                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.onRefreshComplete();
                                closeNetDialog();
                            }
                        });
                    }
                });
    }

    private void drawViews(ErrandOrder order) {
        mErrandOrder = order;
        mOrderIdTv.setText(order.order_id);
        mScheduleLayout.setStatus(order);

        if (null != order
                && !StringUtils.getInstance().isNullOrEmpty(order.create_time)) {
            mOrderTimeTv.setText(order.create_time);
        }

        if (null != order
                && !StringUtils.getInstance().isNullOrEmpty(order.total_money)) {
            mTotalPriceTv.setText("$" + order.total_money);
            mPriceLayout.setVisibility(View.VISIBLE);
            mNotFeedbackTipTv.setVisibility(View.GONE);
        } else {
            mPriceLayout.setVisibility(View.GONE);
            mNotFeedbackTipTv.setVisibility(View.VISIBLE);
        }

        StringUtils.getInstance().setText(order.cust_name, mCustNameTv);
        StringUtils.getInstance().setText(order.cust_phone, mCustPhoneTv);
        StringUtils.getInstance().setText(order.cust_address, mCustAddressTv);
        StringUtils.getInstance().setText(order.remark, mRemarkTv);

        if (null != order.imgs && order.imgs.size() > 0) {
            mPictureLayout.setVisibility(View.VISIBLE);

            int dpToPx = ExtendUtils.getInstance().dip2px(this, 10);
            int mWidth = (AppConfig.getScreenWidth() - (ErrandHomeActivity.NUM_IN_LINE + 1) * dpToPx) / ErrandHomeActivity.NUM_IN_LINE;
            int mHeight = mWidth;

            mPictureLayout.setNumInLine(ErrandHomeActivity.NUM_IN_LINE);
            mPictureLayout.setLimitSize(ErrandHomeActivity.LIMIT_SIZE);
            mPictureLayout.setIsView(true);
            mPictureLayout.setDimensions(mWidth, mHeight);
            mPictureLayout.addPictures(order.imgs);
        } else {
            mPictureLayout.setVisibility(View.GONE);
        }

        if ((!StringUtils.getInstance().isNullOrEmpty(order.pay_state)
                && order.pay_state.equals(FusionCode.PayStatus.PAY_STATUS_PAIED))
                || order.state.equals(FusionCode.OrderStatus.ORDER_STATUS_CANCELLED)) {
            mBtnLayout.setVisibility(View.GONE);
        } else {
            mBtnLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void addMarker(ExpLngLat location) {
        if (null == location
                || StringUtils.getInstance().isNullOrEmpty(location.lat)
                || StringUtils.getInstance().isNullOrEmpty(location.lng)) {
            return;
        }


        mMap.clear();

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.layout_customer_marker, null);
        LatLng latLng = new LatLng(Double.valueOf(location.lat), Double.valueOf(location.lng));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(ExtendUtils.getInstance()
                        .createDrawableFromView(ErrandOrderDetailActivity.this, marker)))
                .position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
    }

    private void getExpLocation() {
        if (null == mMap || StringUtils.getInstance().isNullOrEmpty(mExpId)) {
            return;
        }

        OkHttpClientManager.getInstance().getExpLocation(Utils.getUserId(this), Utils.getToken(this), mExpId,
                new WtNetWorkListener<ExpLngLat>() {
                    @Override
                    public void onSucess(RemoteReturnData<ExpLngLat> data) {
                        if (null != data && null != data.data) {
                            addMarker(data.data);
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(ErrandOrderDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    @OnClick({R.id.layout_back, R.id.layout_map_title,
            R.id.layout_map_layer, R.id.layout_refresh_location,
            R.id.btn_cancel, R.id.btn_pay})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                ErrandOrderDetailActivity.this.finish();
                break;
            }

            case R.id.layout_map_title: {
                if (mMapLayout.getVisibility() == View.VISIBLE) {
                    mMapLayout.setVisibility(View.GONE);
                    mMapLayerLayout.setVisibility(View.GONE);
                    mArrowIv.setImageResource(R.mipmap.ic_map_down_arrow);
                } else {
                    mMapLayout.setVisibility(View.VISIBLE);
                    mMapLayerLayout.setVisibility(View.VISIBLE);
                    mArrowIv.setImageResource(R.mipmap.ic_map_up_arrow);
                }
                break;
            }

            case R.id.layout_map_layer: {
                MapActivity.invoke(ErrandOrderDetailActivity.this, mExpId);
                break;
            }

            case R.id.layout_refresh_location: {
                getExpLocation();
                break;
            }

            case R.id.btn_cancel: {
                // 取消订单
                AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(ErrandOrderDetailActivity.this, true);
                builder.setMessage(R.string.confirm_cancel)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 取消订单
                                doCancelOrder();
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

            case R.id.btn_pay: {
                // 支付订单
                if (mInPay) {
                    PromptUtils.getInstance().showShortPromptToast(this, R.string.paying);
                    return;
                }
                mInPay = true;
                choosePayments(true);
                break;
            }

            default: {
                break;
            }
        }
    }

    private void doCancelOrder() {
        if (StringUtils.getInstance().isNullOrEmpty(mOrderId)) {
            return;
        }

        showNetDialog("");
        OkHttpClientManager.getInstance().cancelErrandOrder(Utils.getUserId(this),
                Utils.getToken(this), mOrderId, FusionCode.OrderStatus.ORDER_STATUS_CANCELLED,
                new WtNetWorkListener<ErrandOrder>() {
                    @Override
                    public void onSucess(RemoteReturnData<ErrandOrder> data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.cancel_success);
                                ErrandOrderDetailActivity.this.finish();
                            }
                        });
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeNetDialog();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
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
                setPaymethod();
            }
        }
    };

    private void setPaymethod() {
        showNetDialog("");
        OkHttpClientManager.getInstance().setErrandPayment(Utils.getUserId(this), Utils.getToken(this),
                mOrderId, mPayMethod, new WtNetWorkListener<ErrandOrder>() {
                    @Override
                    public void onSucess(RemoteReturnData<ErrandOrder> data) {
                        if (null != data && null != data.data) {
                            // 成功
                            if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_CASH) ||
                                    mPayMethod.equals(FusionCode.PayMethod.PAY_REWARD_POINT)) {
                                // 现金支付
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.pay_success);
                                        ErrandOrderDetailActivity.this.finish();
                                    }
                                });
                            } else {
                                doPay();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mInPay = false;
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String status, final String msg_cn, final String msg_en) {
                        mInPay = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                responseError(msg_cn, msg_en);
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

    private void doPay() {
        if (null == mErrandOrder
                || StringUtils.getInstance().isNullOrEmpty(mErrandOrder.order_id)) {
            mInPay = false;
            return;
        }

        if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_PAYPAL)) {
            doPaypalPay(mErrandOrder);
        } else if (mPayMethod.equals(FusionCode.PayMethod.PAY_METHOD_CREDIT_CARD)) {
            doCreditCardPay(mErrandOrder);
        }
    }

    private void doCreditCardPay(ErrandOrder order) {
        if (null == order
                || StringUtils.getInstance().isNullOrEmpty(order.total_money)
                || StringUtils.getInstance().isNullOrEmpty(order.order_id)) {
            mInPay = false;
            return;
        }

        Intent intent = new Intent(ErrandOrderDetailActivity.this, CardListActivity.class);
        intent.putExtra(CardListActivity.INTENT_IS_CHOOSE, true);
        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE);
    }

    public void doPaypalPay(ErrandOrder order) {
        if (null == order
                || StringUtils.getInstance().isNullOrEmpty(order.total_money)
                || StringUtils.getInstance().isNullOrEmpty(order.order_id)) {
            mInPay = false;
            return;
        }

        //创建支付对象，用于传过去给PayPal服务器进行收款
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, order.total_money);
        Intent intent = new Intent(ErrandOrderDetailActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        //这里直接调起PayPal的sdk进行付款操作
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    //这里只传一个总价格或者单个产品的信息收款情况
    private PayPalPayment getThingToBuy(String paymentIntent, String money) {
        return new PayPalPayment(new BigDecimal(money), "USD", "Total", paymentIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(ErrandOrderDetailActivity.this, PayPalService.class));
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
                mInPay = false;
                // 交易取消
                payPalNotify("");
            } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
                // 信用卡支付
                mInPay = false;
                PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.pay_cancel);
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                payPalNotify("");
            } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
                // 信用卡支付
                mInPay = false;
                PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.pay_cancel);
            }
        }
    }

    private void payPalNotify(String notify) {
        PaypalNotify paypalNotify = new PaypalNotify();
        if (!StringUtils.getInstance().isNullOrEmpty(notify)) {
            paypalNotify.notify = notify;
        }
        paypalNotify.order_id = mOrderId;

        OkHttpClientManager.getInstance().paypalNotifyErrand(paypalNotify, new WtNetWorkListener<JsonElement>() {
            @Override
            public void onSucess(RemoteReturnData<JsonElement> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInPay = false;
                        PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.pay_success);
                        ErrandOrderDetailActivity.this.finish();
                    }
                });

            }

            @Override
            public void onError(final String status, final String msg_cn, final String msg_en) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseError(msg_cn, msg_en);
                        mInPay = false;
                        LoginActivity.loginError(ErrandOrderDetailActivity.this, status);
                    }
                });

            }

            @Override
            public void onFinished() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInPay = false;
                    }
                });
            }
        });
    }

    @Override
    public void createStripeTokenSuccess(Token token) {
        showNetDialog("");
        OkHttpClientManager.getInstance().creditCardPay(mErrandOrder.order_id,
                token.getId(), "1", new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mInPay = false;
                                PromptUtils.getInstance().showShortPromptToast(ErrandOrderDetailActivity.this, R.string.pay_success);
                                ErrandOrderDetailActivity.this.finish();
                            }
                        });

                    }

                    @Override
                    public void onError(String status, final String msg_cn, final String msg_en) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mInPay = false;
                                responseError(msg_cn, msg_en);
                            }
                        });
                    }

                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mInPay = false;
                                closeNetDialog();
                            }
                        });
                    }
                });
    }

    @Override
    public void createStripeTokenFail() {
        mInPay = false;
    }

    public static void invoke(Context context, String orderId) {
        if (null == context
                || StringUtils.getInstance().isNullOrEmpty(orderId)) {
            return;
        }

        Intent intent = new Intent(context, ErrandOrderDetailActivity.class);
        intent.putExtra(INTENT_ORDER_ID, orderId);
        context.startActivity(intent);
    }
}
