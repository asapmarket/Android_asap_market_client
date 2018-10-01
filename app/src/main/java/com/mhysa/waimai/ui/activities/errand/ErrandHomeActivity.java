package com.mhysa.waimai.ui.activities.errand;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.config.AppConfig;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.UploadImgUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.model.errand.ErrandCreateOrder;
import com.mhysa.waimai.model.errand.ErrandOrder;
import com.mhysa.waimai.model.errand.ErrandRewardPoint;
import com.mhysa.waimai.model.upload.UploadInfo;
import com.mhysa.waimai.model.wallet.RewardPoint;
import com.mhysa.waimai.ui.activities.base.BaseUploadImageActivity;
import com.mhysa.waimai.ui.activities.personal.AddressListActivity;
import com.mhysa.waimai.ui.customerviews.addpicture.AddPictureLayout;
import com.mhysa.waimai.utils.PromptDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.net.URI;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2018/3/13
 *
 * @author xusheng
 */

public class ErrandHomeActivity extends BaseUploadImageActivity {

    public static int NUM_IN_LINE = 5;

    public static int LIMIT_SIZE = 5;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.layout_add_pictures)
    AddPictureLayout mAddPictureLayout;

    @Bind(R.id.et_description)
    EditText mDescriptionEt;

    @Bind(R.id.tv_address)
    TextView mAddressTv;

    @Bind(R.id.tv_reward_point_insufficient)
    TextView mRewardPointInsufficientTv;

    @Bind(R.id.tv_errand_tip)
    TextView mErrandTipTv;

    private Address mAddress;

    private Dialog mHowDialog = null;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_errand_home;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.errand_title);
        mViewDivider.setVisibility(View.GONE);
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        int dpToPx = ExtendUtils.getInstance().dip2px(this, 10);
        int width = (AppConfig.getScreenWidth() - (NUM_IN_LINE + 3) * dpToPx) / NUM_IN_LINE;
        mAddPictureLayout.setNumInLine(NUM_IN_LINE);
        mAddPictureLayout.setLimitSize(LIMIT_SIZE);
        mAddPictureLayout.setDimensions(width, width);
        mAddPictureLayout.setListener(new AddPictureLayout.OnOperateListener() {
            @Override
            public void onAddClick() {
                showMenu(720, 1280, 720, 1280);
            }
        });
        mAddPictureLayout.addFile(null);

        final Drawable drawable = getResources().getDrawable(R.mipmap.ic_errand_edit);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        mDescriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    mDescriptionEt.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mDescriptionEt.setCompoundDrawables(drawable, null, null, null);
                }
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        EventBus.getDefault().register(this);

        getErrandRewardPoint();
    }

    private void getErrandRewardPoint() {
        OkHttpClientManager.getInstance().getErrandRewardPoint(Utils.getToken(this), Utils.getUserId(this),
                new WtNetWorkListener<ErrandRewardPoint>() {
                    @Override
                    public void onSucess(final RemoteReturnData<ErrandRewardPoint> data) {
                        if (null != data && null != data.data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawViews(data.data);
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
    }

    private void drawViews(ErrandRewardPoint point) {
        mErrandTipTv.setText(getResources().getString(R.string.errand_tip, "" + point.use_point));

        try {
            double usePoint = Double.valueOf(point.use_point);
            if (usePoint > point.user_point) {
                mRewardPointInsufficientTv.setVisibility(View.VISIBLE);
            } else {
                mRewardPointInsufficientTv.setVisibility(View.INVISIBLE);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @OnClick({R.id.layout_back, R.id.layout_address,
            R.id.tv_take_order, R.id.tv_how})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                ErrandHomeActivity.this.finish();
                break;
            }

            case R.id.layout_address: {
                AddressListActivity.invoke(ErrandHomeActivity.this, true);
                break;
            }

            case R.id.tv_take_order: {
                order();
                break;
            }

            case R.id.tv_how: {
                showHowDialog();
                break;
            }

            default: {
                break;
            }
        }
    }

    private void showHowDialog() {
        if (null != mHowDialog && !mHowDialog.isShowing()) {
            mHowDialog.show();
            return;
        }

        getRewardPoint();
    }

    private void getRewardPoint() {
        OkHttpClientManager.getInstance().getHowToGetRewardPoint(
                Utils.getUserId(this),
                Utils.getToken(this),
                new WtNetWorkListener<RewardPoint>() {
                    @Override
                    public void onSucess(final RemoteReturnData<RewardPoint> data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != data && null != data.data) {
                                    showTipDialog(data.data);
                                }
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
                    }
                }
        );
    }

    private void showTipDialog(RewardPoint rewardPoint) {
        if (null != rewardPoint) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_rewardpoint_tip, null);
            view.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHowDialog.dismiss();
                }
            });

            TextView tipTv = (TextView) view.findViewById(R.id.tv_message);
            tipTv.setText(Utils.isChinese(this) ? rewardPoint.instructions_cn : rewardPoint.instructions_en);

            PromptDialogBuilder builder = new PromptDialogBuilder(this);
            builder.setIsHasBackground(false);
            builder.setView(view);
            mHowDialog = builder.create();
            mHowDialog.show();
        }
    }

    private void order() {
        if (mRewardPointInsufficientTv.getVisibility() == View.VISIBLE) {
            PromptUtils.getInstance().showShortPromptToast(ErrandHomeActivity.this, R.string.rewardpoint_insufficient);
            return;
        }

        String remark = mDescriptionEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(remark)) {
            PromptUtils.getInstance().showShortPromptToast(ErrandHomeActivity.this, R.string.errand_description_hint);
            return;
        }

        if (null == mAddress) {
            PromptUtils.getInstance().showShortPromptToast(ErrandHomeActivity.this, R.string.choose_address_hint);
            return;
        }

        ErrandCreateOrder errandCreateOrder = new ErrandCreateOrder();
        errandCreateOrder.user_id = Utils.getUserId(this);
        errandCreateOrder.token = Utils.getToken(this);
        ErrandOrder errandOrder = new ErrandOrder();
        errandOrder.remark = remark;
        errandOrder.extm_id = mAddress.extm_id;

        errandCreateOrder.order = errandOrder;

        showNetDialog("");
        OkHttpClientManager.getInstance().errandCreateOrder(errandCreateOrder, new WtNetWorkListener<ErrandOrder>() {
            @Override
            public void onSucess(final RemoteReturnData<ErrandOrder> data) {
                if (null != data
                        && null != data.data
                        && !StringUtils.getInstance().isNullOrEmpty(data.data.order_id)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImages(data.data.order_id);
                        }
                    });
                }
            }

            @Override
            public void onError(String status, String msg_cn, String msg_en) {
                responseError(msg_cn, msg_en);
            }

            @Override
            public void onFinished() {
                closeNetDialog();
            }
        });
    }

    private void uploadImages(String id) {
        if (null == mAddPictureLayout.getPictures() || mAddPictureLayout.getPictures().size() == 0) {
            PromptUtils.getInstance().showShortPromptToast(ErrandHomeActivity.this, R.string.pay_success);
            ErrandHomeActivity.this.finish();
            return;
        }

        final List<Uri> uris = mAddPictureLayout.getPictures();
        final int size = uris.size();
        for (int i = 0; i < size; i++) {
            Uri uri = uris.get(i);
            try {
                File file = new File(new URI(uri.toString()));
                UploadImgUtils uploadImgUtils = new UploadImgUtils(file.getAbsolutePath(), file.getAbsolutePath());
                OkHttpClientManager.getInstance().uploadInfo(uploadImgUtils.getUploadFileName(), "tb_order_errands",
                        id, file, new WtNetWorkListener<UploadInfo>() {
                            @Override
                            public void onSucess(RemoteReturnData<UploadInfo> data) {

                            }

                            @Override
                            public void onError(String status, String msg_cn, String msg_en) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        PromptUtils.getInstance().showShortPromptToast(ErrandHomeActivity.this, R.string.pay_success);
        ErrandHomeActivity.this.finish();
    }

    @Override
    public void selectPhotoSuccess(Uri uri) {
        if (null == uri) {
            return;
        }
        mAddPictureLayout.addFile(uri);
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
                    mAddress = (Address) event.getData();
                    if (null != mAddress) {
                        StringUtils.getInstance().setText(mAddress.address, mAddressTv);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ErrandHomeActivity.class));
    }
}
