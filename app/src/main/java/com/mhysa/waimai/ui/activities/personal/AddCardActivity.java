package com.mhysa.waimai.ui.activities.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.db.CardLogicImp;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.stripe.android.model.Card;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/9/19
 *
 * @author xusheng
 */

public class AddCardActivity extends BaseActivity {

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.et_card_number)
    EditText mCardNumberEt;

    @Bind(R.id.et_year)
    EditText mYearEt;

    @Bind(R.id.et_month)
    EditText mMonthEt;

    @Bind(R.id.et_cvv)
    EditText mCvvEt;

    @Bind(R.id.et_address)
    EditText mAddressEt;

    @Bind(R.id.et_first_name)
    EditText mFirstNameEt;

    @Bind(R.id.et_last_name)
    EditText mLastNameEt;

    @Bind(R.id.et_zipcode)
    EditText mZipcodeEt;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case 0: {
                    EventBus.getDefault().post(new ApplicationEvent(EventID.ADD_CARD_SUCCESS));
                    AddCardActivity.this.finish();
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
        return R.layout.activity_add_card;
    }

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.add_credit_card);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }

    @OnClick({R.id.layout_back, R.id.btn_commit})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                AddCardActivity.this.finish();
                break;
            }

            case R.id.btn_commit: {
                commit();
                break;
            }

            default: {
                break;
            }
        }
    }

    private void commit() {
        final String cardNumber = mCardNumberEt.getText().toString().trim();
        String year = mYearEt.getText().toString().trim();
        String month = mMonthEt.getText().toString().trim();
        final String cvv = mCvvEt.getText().toString().trim();
        final String address = mAddressEt.getText().toString().trim();
        final String firstName = mFirstNameEt.getText().toString().trim();
        final String lastName = mLastNameEt.getText().toString().trim();
        final String zipCode = mZipcodeEt.getText().toString().trim();
        if (StringUtils.getInstance().isNullOrEmpty(cardNumber)
                || StringUtils.getInstance().isNullOrEmpty(year)
                || StringUtils.getInstance().isNullOrEmpty(month)
                || StringUtils.getInstance().isNullOrEmpty(cvv)
                || StringUtils.getInstance().isNullOrEmpty(address)
                || StringUtils.getInstance().isNullOrEmpty(firstName)
                || StringUtils.getInstance().isNullOrEmpty(lastName)
                || StringUtils.getInstance().isNullOrEmpty(zipCode)) {
            PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this,
                    R.string.configuration_is_uncomplete);
            return;
        }

//        card.card_no = "4242424242424242";
//        card.year = "2020";
//        card.month = "10";
//        card.cvv = "444";

        try {
            final int monthInt = Integer.valueOf(month);
            final int yearInt = Integer.valueOf(year);

            Card card = new Card(cardNumber, monthInt, yearInt, cvv);
            if (card.validateCard()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        com.mhysa.waimai.model.card.Card saveCard = new com.mhysa.waimai.model.card.Card();
                        saveCard.user_id = Utils.getUserId(AddCardActivity.this);
                        saveCard.card_no = cardNumber;
                        saveCard.month = monthInt;
                        saveCard.year = yearInt;
                        saveCard.cvv = cvv;
                        saveCard.address = address;
                        saveCard.firstName = firstName;
                        saveCard.lastName = lastName;
                        saveCard.zipCode = zipCode;
                        CardLogicImp.addCard(saveCard);
                        mHandler.sendEmptyMessage(0);
                    }
                }).start();
            } else {
                if (!card.validateNumber()) {
                    PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this, R.string.card_number_invalid);
                } else if (!card.validateExpiryDate()) {
                    PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this, R.string.card_expiry_date_invalid);
                } else if (!card.validateCVC()) {
                    PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this, R.string.card_cvv_invalid);
                } else {
                    PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this, R.string.card_detail_invalid);
                }
            }


//            String expirationDate = "";
//            if (monthInt < 10) {
//                expirationDate = "0" + String.valueOf(monthInt) + year;
//            } else {
//                expirationDate = month + year;
//            }

//            OkHttpClientManager.getInstance().saveCard(Utils.getUserId(this),
//                    Utils.getToken(this), cardNumber, expirationDate,
//                    cvv, address, firstName, lastName,zipCode,
//                    new WtNetWorkListener<JsonElement>() {
//                        @Override
//                        public void onSucess(RemoteReturnData<JsonElement> data) {
//                            if (null != data) {
//                                EventBus.getDefault().post(new ApplicationEvent(EventID.ADD_CARD_SUCCESS));
//                                AddCardActivity.this.finish();
//                            }
//                        }
//
//                        @Override
//                        public void onError(String status, String msg_cn, String msg_en) {
//                            LoginActivity.loginError(AddCardActivity.this, status);
//                            responseError(msg_cn, msg_en);
//                            LoginActivity.loginError(AddCardActivity.this, status);
//                        }
//
//                        @Override
//                        public void onFinished() {
//                            closeNetDialog();
//                        }
//                    });

        } catch (Exception exception) {
            PromptUtils.getInstance().showShortPromptToast(AddCardActivity.this,
                    R.string.configuration_is_uncomplete);
        }
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, AddCardActivity.class);
        context.startActivity(intent);
    }
}
