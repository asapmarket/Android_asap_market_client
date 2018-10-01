package com.mhysa.waimai.ui.activities.personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * 编辑收货地址
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class EditAddressActivity extends BaseActivity {

    private static final String INTENT_ADDRESS = "intent_address";

    private static final String INTENT_POSITION = "intent_position";

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

    private String mDetailAddress;

    private String mSex;

    private Address mAddress;

    private int mPosition;

    @Override
    protected void getIntentData() {
        super.getIntentData();

        if (null != getIntent()) {
            mAddress = (Address) getIntent().getSerializableExtra(INTENT_ADDRESS);
            mPosition = getIntent().getIntExtra(INTENT_POSITION, 0);
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_edit_address;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.edit_address);
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

        if (null != mAddress) {
            StringUtils.getInstance().setText(mAddress.extm_name, mNameEt);
            StringUtils.getInstance().setText(mAddress.extm_phone, mPhoneEt);
            StringUtils.getInstance().setText(mAddress.zip_code, mMailEt);
            StringUtils.getInstance().setText(mAddress.address, mAddressEt);

            if (!StringUtils.getInstance().isNullOrEmpty(mAddress.sex)
                    && mAddress.sex.equals(FusionCode.Sex.MALE)) {
                mMaleCb.setChecked(true);
                mFemaleCb.setChecked(false);
            } else {
                mFemaleCb.setChecked(true);
                mMaleCb.setChecked(false);
            }
        }
    }

    @OnClick({R.id.layout_back, R.id.btn_save, R.id.btn_delete})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                EditAddressActivity.this.finish();
                break;
            }

            case R.id.btn_save: {
                verify();
                break;
            }

            case R.id.btn_delete: {
                delete();
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

        mDetailAddress = mAddressEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(mDetailAddress)) {
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
                    editAddress();
                } else {
                    closeNetDialog();
                    PromptUtils.getInstance().showShortPromptToast(EditAddressActivity.this, R.string.mail_is_not_in_range);
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {
                LoginActivity.loginError(EditAddressActivity.this, status);
                closeNetDialog();
                PromptUtils.getInstance().showShortPromptToast(EditAddressActivity.this, R.string.mail_is_not_in_range);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void editAddress() {
        OkHttpClientManager.getInstance().updateAddress(Utils.getUserId(this),
                Utils.getToken(this), mPhone, mContact, mSex, mDetailAddress,
                mAddress.extm_id, mAddress.is_default, mMail,
                new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        if (null != data) {
                            PromptUtils.getInstance().showShortPromptToast(EditAddressActivity.this,
                                    R.string.edit_address_success);
                            mAddress.sex = mSex;
                            mAddress.extm_name = mContact;
                            mAddress.extm_phone = mPhone;
                            mAddress.zip_code = mMail;
                            mAddress.address = mDetailAddress;
                            EventBus.getDefault().post(new ApplicationEvent(EventID.EDIT_ADDRESS_SUCCESS, mPosition + "", mAddress));
                            EditAddressActivity.this.finish();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(EditAddressActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                        PromptUtils.getInstance().showShortPromptToast(EditAddressActivity.this,
                                R.string.edit_address_success);
                        mAddress.sex = mSex;
                        mAddress.extm_name = mContact;
                        mAddress.extm_phone = mPhone;
                        mAddress.zip_code = mMail;
                        mAddress.address = mDetailAddress;
                        EventBus.getDefault().post(new ApplicationEvent(EventID.EDIT_ADDRESS_SUCCESS, mPosition + "", mAddress));
                        EditAddressActivity.this.finish();
                    }
                });
    }

    private void delete() {
        if (null == mAddress || StringUtils.getInstance().isNullOrEmpty(mAddress.extm_id)) {
            return;
        }

        AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(this, true);
        builder.setTitle(R.string.tip);
        builder.setMessage(R.string.confirm_delete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void doDelete() {
        showNetDialog("");
        OkHttpClientManager.getInstance().deleteAddress(Utils.getUserId(this),
                Utils.getToken(this), mAddress.extm_id, new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        if (null != data) {
                            EventBus.getDefault().post(new ApplicationEvent(EventID.DELETE_ADDRESS_SUCCESS,
                                    mPosition));
                            EditAddressActivity.this.finish();
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(EditAddressActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        closeNetDialog();
                        EditAddressActivity.this.finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    public static void invoke(Context context, Address address, int position) {
        Intent intent = new Intent(context, EditAddressActivity.class);
        intent.putExtra(INTENT_ADDRESS, address);
        intent.putExtra(INTENT_POSITION, position);
        context.startActivity(intent);
    }
}
