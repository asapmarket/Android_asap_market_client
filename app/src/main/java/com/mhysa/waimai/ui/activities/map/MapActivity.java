package com.mhysa.waimai.ui.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.user.ExpLngLat;

/**
 * 文件描述
 * Date: 2017/8/9
 *
 * @author xusheng
 */

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    // 快递员的id
    public static final String INTENT_EXP_ID = "intent_exp_id";

    private String mExpId;

    private GoogleMap mMap = null;

    private View mMarkView;

    private int i = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case 0: {
                    getExpLocation();

                    if (null != mHandler) {
                        mHandler.sendEmptyMessageDelayed(0, 60 * 1000);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_map;
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();

        if (null != getIntent()) {
            mExpId = getIntent().getStringExtra(INTENT_EXP_ID);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getExpLocation() {
        if (StringUtils.getInstance().isNullOrEmpty(mExpId)) {
            return;
        }

        OkHttpClientManager.getInstance().getExpLocation(Utils.getUserId(this),
                Utils.getToken(this), mExpId, new WtNetWorkListener<ExpLngLat>() {
                    @Override
                    public void onSucess(RemoteReturnData<ExpLngLat> data) {
                        if (null != data && null != data.data) {
                            moveCamera(data.data);
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {

                    }

                    @Override
                    public void onFinished() {
                        if (i % 3 == 0) {
                            ExpLngLat expLngLat = new ExpLngLat();
                            expLngLat.lat = "31.9479068468";
                            expLngLat.lng = "118.8141148756";
                            moveCamera(expLngLat);
                        }

                        if (i % 3 == 1) {
                            ExpLngLat expLngLat = new ExpLngLat();
                            expLngLat.lat = "32.0362874149";
                            expLngLat.lng = "118.7487068743";
                            moveCamera(expLngLat);
                        } else {
                            ExpLngLat expLngLat = new ExpLngLat();
                            expLngLat.lat = "32.0394747449";
                            expLngLat.lng = "118.7840660043";
                            moveCamera(expLngLat);
                        }

                        i++;
                    }
                });
    }

    private void moveCamera(ExpLngLat expLngLat) {
        if (null == mMarkView) {
            mMarkView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_customer_marker, null);
        }

        mMap.clear();

        LatLng latLng = new LatLng(Double.valueOf(expLngLat.lat), Double.valueOf(expLngLat.lng));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(ExtendUtils.getInstance()
                        .createDrawableFromView(MapActivity.this, mMarkView)))
                .position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        if (null != mHandler) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mHandler) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
        super.onBackPressed();
    }

    public static void invoke(Context context, String expId) {
        if (null == context || StringUtils.getInstance().isNullOrEmpty(expId)) {
            return;
        }

        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(INTENT_EXP_ID, expId);
        context.startActivity(intent);
    }
}
