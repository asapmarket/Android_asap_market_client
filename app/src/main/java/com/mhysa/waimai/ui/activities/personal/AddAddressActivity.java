package com.mhysa.waimai.ui.activities.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.model.address.Range;
import com.mhysa.waimai.ui.activities.login.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加收货地址
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class AddAddressActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.cb_male)
    CheckBox mMaleCb;

    @Bind(R.id.cb_female)
    CheckBox mFemaleCb;

    @Bind(R.id.layout_male)
    LinearLayout mMaleLayout;

    @Bind(R.id.layout_female)
    LinearLayout mFemaleLayout;

    @Bind(R.id.et_contact)
    EditText mNameEt;

    @Bind(R.id.et_phone)
    EditText mPhoneEt;

    @Bind(R.id.et_mail)
    EditText mMailEt;

    @Bind(R.id.et_address)
    EditText mAddressEt;

    private String mContact;

    private String mPhone;

    private String mMail;

    private String mAddress;

    private String mSex;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_add_address;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.add_address);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mMaleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMaleChecked = mMaleCb.isChecked();
                if (!isMaleChecked) {
                    mMaleCb.setChecked(true);
                    mFemaleCb.setChecked(false);
                }
            }
        });

        mFemaleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMaleChecked = mFemaleCb.isChecked();
                if (!isMaleChecked) {
                    mMaleCb.setChecked(false);
                    mFemaleCb.setChecked(true);
                }
            }
        });
    }

    @OnClick({R.id.layout_back, R.id.btn_save})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                AddAddressActivity.this.finish();
                break;
            }

            case R.id.btn_save: {
                verify();
                break;
            }

            default: {
                break;
            }
        }
    }

    private void verify() {
        mContact = mNameEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(mContact)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.contact_is_null);
            return;
        }

        mPhone = mPhoneEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(mPhone)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.phone_is_null);
            return;
        }

        mMail = mMailEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(mMail)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.email_is_null);
            return;
        }

        mAddress = mAddressEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(mAddress)) {
            PromptUtils.getInstance().showShortPromptToast(this, R.string.address_is_null);
            return;
        }

        mSex = mMaleCb.isChecked() ? FusionCode.Sex.MALE : FusionCode.Sex.FEMALE;

        showNetDialog("");
        // 首先判断是否在配送范围内
        OkHttpClientManager.getInstance().inRange(mMail, new WtNetWorkListener<Range>() {
            @Override
            public void onSucess(RemoteReturnData<Range> data) {
                if (null != data
                        && null != data.data
                        && data.data.in_range == FusionCode.InRange.IN_RANGE) {
                    addAddress();
                } else {
                    closeNetDialog();
                    PromptUtils.getInstance().showShortPromptToast(AddAddressActivity.this, R.string.mail_is_not_in_range);
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {
                LoginActivity.loginError(AddAddressActivity.this, status);
                closeNetDialog();
                PromptUtils.getInstance().showShortPromptToast(AddAddressActivity.this, R.string.mail_is_not_in_range);
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void addAddress() {
        OkHttpClientManager.getInstance().addAddress(Utils.getUserId(this),
                Utils.getToken(this), mPhone, mContact, mSex, mAddress, mMail,
                new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        if (null != data) {
                            PromptUtils.getInstance().showShortPromptToast(AddAddressActivity.this,
                                    R.string.add_address_success);
                            Address address = new Address();
                            address.sex = mSex;
                            address.is_default = FusionCode.DefaultAddress.IS_NOT_DEFAULT;
                            address.extm_name = mContact;
                            address.extm_phone = mPhone;
                            address.zip_code = mMail;
                            address.address = mAddress;
                            EventBus.getDefault().post(new ApplicationEvent(EventID.ADD_ADDRESS_SUCCESS, address));
                            AddAddressActivity.this.finish();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        LoginActivity.loginError(AddAddressActivity.this, status);
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(AddAddressActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddAddressActivity.class);
        context.startActivity(intent);
    }
}
