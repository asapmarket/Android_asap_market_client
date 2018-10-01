package com.mhysa.waimai.ui.activities.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.listview.XListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.model.address.AddressList;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.personal.AddressListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 收货地址列表
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class AddressListActivity extends BaseActivity {

    private static final String INTENT_IS_CHOOSE = "intent_is_choose";

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.lv_address)
    XListView mAddressLv;

    private AddressListAdapter mAdapter;

    private ArrayList<Address> mAddresses = new ArrayList<Address>();

    private boolean mIsChoose;

    @Override
    protected void getIntentData() {
        super.getIntentData();

        if (null != getIntent()) {
            mIsChoose = getIntent().getBooleanExtra(INTENT_IS_CHOOSE, false);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        mAddressLv.setPullLoadEnable(false);
        mAddressLv.setPullRefreshEnable(false);

        mAddressLv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                getData();
            }

            @Override
            public void onLoadMore() {

            }
        });
        mAdapter = new AddressListAdapter(AddressListActivity.this, mAddresses, mIsChoose);

        mAdapter.setListener(new AddressListAdapter.AddressListListener() {
            @Override
            public void onChoose(Address address) {
                if (null != address) {
                    EventBus.getDefault().post(new ApplicationEvent(EventID.CHOOSE_ADDRESS_SUCCESS, address));
                    finish();
                }
            }

            @Override
            public void onSetDefault(Address address, int position) {
                setDefaultAddress(address, position);
            }
        });
        mAddressLv.setAdapter(mAdapter);
    }

    private void setDefaultAddress(final Address address, final int position) {
        address.is_default = FusionCode.DefaultAddress.IS_DEFAULT;
        OkHttpClientManager.getInstance().updateAddress(Utils.getUserId(this),
                Utils.getToken(this),
                address.extm_phone,
                address.extm_name,
                address.sex,
                address.address,
                address.extm_id,
                address.zip_code,
                FusionCode.DefaultAddress.IS_DEFAULT,
                new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        if (null != data) {
                            final int size = mAddresses.size();
                            for (int i = 0; i < size; i++) {
                                if (i == position) {
                                    mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_DEFAULT;
                                } else {
                                    mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_NOT_DEFAULT;
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(AddressListActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        final int size = mAddresses.size();
                        for (int i = 0; i < size; i++) {
                            if (i == position) {
                                mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_DEFAULT;
                            } else {
                                mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_NOT_DEFAULT;
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        showNetDialog("");
        getData();

        EventBus.getDefault().register(this);
    }

    /**
     * 获取收货地址列表
     */
    private void getData() {
        OkHttpClientManager.getInstance().getAddressList(Utils.getUserId(this),
                Utils.getToken(this),
                new WtNetWorkListener<AddressList>() {
                    @Override
                    public void onSucess(RemoteReturnData<AddressList> data) {
                        if (null != data
                                && null != data.data
                                && data.data.address_list.size() > 0) {
                            mAddresses.clear();
                            mAddresses.addAll(data.data.address_list);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(AddressListActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                        mAddressLv.stopRefresh();
                        mAddressLv.stopLoadMore();
                    }
                });
    }

    @OnClick({R.id.layout_back, R.id.layout_add})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                AddressListActivity.this.finish();
                break;
            }

            case R.id.layout_add: {
                AddAddressActivity.invoke(AddressListActivity.this);
                break;
            }

            default: {
                break;
            }
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
            case EventID.ADD_ADDRESS_SUCCESS: {
                getData();
                break;
            }

            case EventID.EDIT_ADDRESS_SUCCESS: {
                try {
                    int position = Integer.valueOf(event.getEventMessage());
                    Address address = (Address) event.getData();
                    address.is_default = FusionCode.DefaultAddress.IS_DEFAULT;

                    if (null != mAddresses && mAddresses.size() > position) {
                        mAddresses.remove(position);
                        mAddresses.add(position, address);

                        final int size = mAddresses.size();
                        for (int i = 0; i < size; i++) {
                            if (i == position) {
                                mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_DEFAULT;
                            } else {
                                mAddresses.get(i).is_default = FusionCode.DefaultAddress.IS_NOT_DEFAULT;
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }

            case EventID.DELETE_ADDRESS_SUCCESS: {
                if (null != event.getData()) {
                    int position = (int) event.getData();
                    if (null != mAddresses && mAddresses.size() > position) {
                        mAddresses.remove(position);
                        mAdapter.notifyDataSetChanged();

                        if (null == mAddresses || mAddresses.size() == 0) {
                            EventBus.getDefault().post(new ApplicationEvent(EventID.ADDRESS_IS_EMPTY));
                        }
                    }
                }
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

    /**
     * @param context
     * @param isChoose 是否是选择收货地址
     */
    public static void invoke(Context context, boolean isChoose) {
        Intent intent = new Intent(context, AddressListActivity.class);
        intent.putExtra(INTENT_IS_CHOOSE, isChoose);
        context.startActivity(intent);
    }
}
