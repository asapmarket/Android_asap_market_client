package com.mhysa.waimai.ui.activities.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.model.user.ExpLngLat;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.map.MapActivity;
import com.mhysa.waimai.ui.customerviews.order.OrderDetailMapLayout;
import com.mhysa.waimai.ui.customerviews.order.OrderDetailStoreItemLayout;
import com.mhysa.waimai.ui.customerviews.order.OrderScheduleLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 订单详情页
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class OrderDetailActivity extends BaseActivity implements OnMapReadyCallback {

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
    OrderScheduleLayout mScheduleLayout;

    @Bind(R.id.layout_store)
    LinearLayout mStoreLayout;

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

    @Bind(R.id.tv_distribution_time)
    TextView mDistributionTimeTv;

    private String mOrderId;

    private String mExpId;

    private GoogleMap mMap = null;

    @Override
    protected void getIntentData() {
        super.getIntentData();
        if (null != getIntent()) {
            mOrderId = getIntent().getStringExtra(INTENT_ORDER_ID);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_order_detail;
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
    }

    private void getOrderDetail() {
        OkHttpClientManager.getInstance().getOrderDetail(Utils.getUserId(this),
                Utils.getToken(this), mOrderId, new WtNetWorkListener<Order>() {
                    @Override
                    public void onSucess(RemoteReturnData<Order> data) {
                        if (null != data && null != data.data) {
                            drawViews(data.data);
                            mExpId = data.data.exp_id;
                            getExpLocation();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(OrderDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        mScrollView.onRefreshComplete();
                        closeNetDialog();
                    }
                });
    }

    private void drawViews(Order order) {
        mOrderIdTv.setText(order.order_id);
        mScheduleLayout.setStatus(false, order.state, order.exp_name);

        if (null != order.store_list
                && order.store_list.size() > 0) {
            mStoreLayout.removeAllViews();
            mStoreLayout.setVisibility(View.VISIBLE);

            final int size = order.store_list.size();
            for (int i = 0; i < size; i++) {
                OrderDetailStoreItemLayout layout = new OrderDetailStoreItemLayout(OrderDetailActivity.this);
                layout.setData(order.store_list.get(i));
                mStoreLayout.addView(layout);
            }
        } else {
            mStoreLayout.removeAllViews();
            mStoreLayout.setVisibility(View.GONE);
        }

        if (null != order
                && !StringUtils.getInstance().isNullOrEmpty(order.create_time)) {
            mOrderTimeTv.setText(order.create_time);
        }

        if (null != order
                && !StringUtils.getInstance().isNullOrEmpty(order.total_money)) {
            mTotalPriceTv.setText("$" + order.total_money);
        }

        if (null != order) {
            if (Utils.isChinese(this)) {
                mPriceDescTv.setText("(包含配送费$" + order.yunfei
                        + "+税费$" + order.tax + ")");
            } else {
                mPriceDescTv.setText("(Include Delivery fee $" + order.yunfei
                        + "+Tax $" + order.tax + ")");
            }
        }

        if (null != order
                && !StringUtils.getInstance().isNullOrEmpty(order.distribution_time)) {
            mDistributionTimeTv.setText(order.distribution_time);
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
                        .createDrawableFromView(OrderDetailActivity.this, marker)))
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
                        LoginActivity.loginError(OrderDetailActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    @OnClick({R.id.layout_back, R.id.layout_map_title,
            R.id.layout_map_layer, R.id.layout_refresh_location})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                OrderDetailActivity.this.finish();
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
                MapActivity.invoke(OrderDetailActivity.this, mExpId);
                break;
            }

            case R.id.layout_refresh_location: {
                getExpLocation();
                break;
            }

            default: {
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    public static void invoke(Context context, String orderId) {
        if (null == context
                || StringUtils.getInstance().isNullOrEmpty(orderId)) {
            return;
        }

        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(INTENT_ORDER_ID, orderId);
        context.startActivity(intent);
    }
}
