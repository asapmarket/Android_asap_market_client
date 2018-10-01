package com.mhysa.waimai.ui.activities.personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonElement;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.fusion.FusionField;
import com.joey.devilfish.fusion.SharedPreferenceConstant;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.SharedPreferenceUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.UploadImgUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.manager.CartManager;
import com.mhysa.waimai.model.upload.UploadInfo;
import com.mhysa.waimai.ui.activities.base.BaseUploadImageActivity;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.activities.main.MainActivity;
import com.mhysa.waimai.ui.activities.password.ChangePwdActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URI;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 账户设置
 * Date: 2017/7/21
 *
 * @author xusheng
 */

public class PersonalSettingActivity extends BaseUploadImageActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.et_nickname)
    EditText mNickNameEt;

    @Bind(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @Bind(R.id.layout_chinese)
    LinearLayout mChineseLayout;

    @Bind(R.id.cb_chinese)
    CheckBox mChineseCb;

    @Bind(R.id.layout_english)
    LinearLayout mEnglishLayout;

    @Bind(R.id.cb_english)
    CheckBox mEnglishCb;

    private int mLastLanguage = -1;

    protected UploadImgUtils mUploadImgUtils = null;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_personal_setting;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.account_setting);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mNickNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = mNickNameEt.getText().toString().trim();
                    if (!StringUtils.getInstance().isNullOrEmpty(content)) {
                        changeNickName(content);
                    } else {
                        PromptUtils.getInstance().showShortPromptToast(PersonalSettingActivity.this, R.string.nickname_is_null);
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mLastLanguage = SharedPreferenceUtils.getSharedPreferences(
                SharedPreferenceConstant.PREFERENCE_NAME, SharedPreferenceConstant.PROPERTY_LANGUAGE,
                PersonalSettingActivity.this, FusionCode.LocalState.LOCAL_STATE_CHINESE);

        if (mLastLanguage == FusionCode.LocalState.LOCAL_STATE_CHINESE) {
            mChineseCb.setChecked(true);
            mEnglishCb.setChecked(false);
        } else {
            mChineseCb.setChecked(false);
            mEnglishCb.setChecked(true);
        }

        StringUtils.getInstance().setText(Utils.getNickName(this), mNickNameEt);

        if (!StringUtils.getInstance().isNullOrEmpty(Utils.getHeadImage(this))) {
            ImageUtils.getInstance().setImageURL(Utils.getHeadImage(this), mAvatarSdv);
        }
    }

    @OnClick({R.id.layout_back, R.id.layout_change_pwd,
            R.id.tv_logout, R.id.sdv_avatar,
            R.id.layout_chinese, R.id.layout_english})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                PersonalSettingActivity.this.finish();
                break;
            }

            case R.id.layout_change_pwd: {
                ChangePwdActivity.invoke(PersonalSettingActivity.this);
                break;
            }

            case R.id.tv_logout: {
                logout();
                break;
            }

            case R.id.sdv_avatar: {
                showMenu(1, 1, 350, 350);
                break;
            }

            case R.id.layout_chinese: {
                mChineseCb.setChecked(true);
                mEnglishCb.setChecked(false);
                if (mLastLanguage == FusionCode.LocalState.LOCAL_STATE_ENGLISH) {
                    mLastLanguage = FusionCode.LocalState.LOCAL_STATE_CHINESE;

                    changeLanguage(mLastLanguage);
                }

                break;
            }

            case R.id.layout_english: {
                mChineseCb.setChecked(false);
                mEnglishCb.setChecked(true);

                if (mLastLanguage == FusionCode.LocalState.LOCAL_STATE_CHINESE) {
                    mLastLanguage = FusionCode.LocalState.LOCAL_STATE_ENGLISH;

                    changeLanguage(mLastLanguage);
                }
                break;
            }

            default: {
                break;
            }
        }
    }

    private void logout() {
        AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(PersonalSettingActivity.this, true);
        builder.setMessage(R.string.confirm_logout)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNetDialog("");
                        Utils.logout(PersonalSettingActivity.this);
                        // 清空购物车
                        CartManager.getInstance().clearCartTable(null);
                        PersonalSettingActivity.this.finish();
                        EventBus.getDefault().post(new ApplicationEvent(EventID.LOGOUT_SUCCESS));
                        closeNetDialog();
                        LoginActivity.invoke(PersonalSettingActivity.this, true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void changeLanguage(int which) {
        FusionField.mCurrentLanguage = which;

        SharedPreferenceUtils.setSharedPreferences(SharedPreferenceConstant.PREFERENCE_NAME,
                SharedPreferenceConstant.PROPERTY_LANGUAGE, which, PersonalSettingActivity.this);

        EventBus.getDefault().post(new ApplicationEvent(EventID.CHANGE_LANGUAGE_SUCCESS));
        MainActivity.invoke(PersonalSettingActivity.this);
        PersonalSettingActivity.this.finish();
    }

    @Override
    public void selectPhotoSuccess(Uri uri) {
        if (null == uri) {
            return;
        }

        try {
            File file = new File(new URI(uri.toString()));

            if (null == file || !file.exists()) {
                return;
            }

            uploadImage(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void uploadImage(File file) {
        showNetDialog("");
        mUploadImgUtils = new UploadImgUtils(file.getAbsolutePath(), file.getAbsolutePath());
        OkHttpClientManager.getInstance().uploadInfo(mUploadImgUtils.getUploadFileName(), file, new WtNetWorkListener<UploadInfo>() {
            @Override
            public void onSucess(RemoteReturnData<UploadInfo> data) {
                // 文件上传成功
                UploadInfo uploadInfo = data.data;
                if (null != uploadInfo
                        && !StringUtils.getInstance().isNullOrEmpty(uploadInfo.fullPath)
                        && !StringUtils.getInstance().isNullOrEmpty(uploadInfo.showImgPath)) {
                    ImageUtils.getInstance().setImageURL(uploadInfo.fullPath, mAvatarSdv);
                    changeAvatar(uploadInfo.showImgPath, uploadInfo.fullPath);
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {
                responseError(msg_cn, msg_en);
                LoginActivity.loginError(PersonalSettingActivity.this, status);
            }

            @Override
            public void onFinished() {
                closeNetDialog();
            }
        });
    }

    private void changeAvatar(final String path, final String fullPath) {
        OkHttpClientManager.getInstance().changeAvatar(path, Utils.getToken(this),
                Utils.getUserId(this), new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        EventBus.getDefault().post(new ApplicationEvent(EventID.CHANGE_AVATAR_SUCCESS, fullPath));
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    private void changeNickName(final String nickname) {
        OkHttpClientManager.getInstance().changeNickname(nickname, Utils.getToken(this),
                Utils.getUserId(this), new WtNetWorkListener<JsonElement>() {
                    @Override
                    public void onSucess(RemoteReturnData<JsonElement> data) {
                        EventBus.getDefault().post(new ApplicationEvent(EventID.CHANGE_NICKNAME_SUCCESS, nickname));
                        PromptUtils.getInstance().showShortPromptToast(PersonalSettingActivity.this, R.string.change_nickname_success);
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(PersonalSettingActivity.this, status);
                    }

                    @Override
                    public void onFinished() {

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
        Intent intent = new Intent(context, PersonalSettingActivity.class);
        context.startActivity(intent);
    }
}
