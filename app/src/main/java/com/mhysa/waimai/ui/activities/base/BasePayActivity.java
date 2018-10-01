package com.mhysa.waimai.ui.activities.base;

import android.app.Dialog;

import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.card.Card;
import com.mhysa.waimai.ui.customerviews.order.PaymethodChooseLayout;
import com.mhysa.waimai.utils.PromptDialogBuilder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

/**
 * 文件描述
 * Date: 2018/3/27
 *
 * @author xusheng
 */

public abstract class BasePayActivity extends BaseActivity {

    // stripe支付 testkey
//    public static final String STRIPE_APIKEY = "pk_test_aCKB2XtfHKuyQjNSydD8dEVx";

    // stripe支付 正式key
    public static final String STRIPE_APIKEY = "pk_live_8byWVolS4Z3jg8esIREiMdpr";

    // 沙盒环境
//    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox environments.
    // 你所注册的APP Id
    // 沙盒账号
//    public static final String CONFIG_CLIENT_ID = "AXCrgSkAyC3E2ePWU6Tph-WJZDd6xfPY9CRTWCusQ7hR6w5SQewvJxPx84kZH7IHiF9mdVVji8bjGYyB";

    public static final String CONFIG_CLIENT_ID = "AWzwurzNKUo-2KhTE1qnkkKA4uep3dg8n-QMVNL32DR1dbmMR5w5wzPvHQjqvPNzDJIhrZ_twZVeXEbq";

    public static final int CREDIT_CARD_REQUEST_CODE = 0;

    public static final int REQUEST_CODE_PAYMENT = 1;

    public static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    public static final int REQUEST_CODE_PROFILE_SHARING = 3;

    public static PayPalConfiguration config
            = new PayPalConfiguration().environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID);
    //以下配置是授权支付的时候用到的
    // .merchantName("Example Merchant")
    // .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
    // .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    public Dialog mChoosePaymethodDialog;

    public String mPayMethod = FusionCode.PayMethod.PAY_METHOD_PAYPAL;

    public double mRewardPoint;

    public abstract PaymethodChooseLayout.OnPaymethodChooseListener getPaymethodListener();

    public abstract void createStripeTokenSuccess(Token token);

    public abstract void createStripeTokenFail();

    /**
     * 选择支付方式
     */
    public void choosePayments(boolean isErrand) {
        if (null != mChoosePaymethodDialog
                && mChoosePaymethodDialog.isShowing()) {
            return;
        }
        PromptDialogBuilder builder = new PromptDialogBuilder(this);
        builder.setIsHasBackground(false);

        PaymethodChooseLayout paymethodChooseLayout = new PaymethodChooseLayout(this);
        paymethodChooseLayout.setIsErrand(isErrand);
        paymethodChooseLayout.setPaymethod(mPayMethod, mRewardPoint);
        paymethodChooseLayout.setListener(getPaymethodListener());
        builder.setView(paymethodChooseLayout);
        mChoosePaymethodDialog = builder.create();
        mChoosePaymethodDialog.show();
    }

    public void createToken(Card card) {
        if (null == card) {
            return;
        }

        showNetDialog("");
        try {
            com.stripe.android.model.Card card1 = new com.stripe.android.model.Card(card.card_no,
                    Integer.valueOf(card.month), Integer.valueOf(card.year), card.cvv);

            if (card1.validateCard()) {
                Stripe stripe = new Stripe(BasePayActivity.this, BasePayActivity.STRIPE_APIKEY);

                stripe.createToken(card1, new TokenCallback() {
                    @Override
                    public void onError(Exception e) {
                        closeNetDialog();
                        PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, e.getLocalizedMessage());
                        createStripeTokenFail();
                    }

                    @Override
                    public void onSuccess(Token token) {
                        closeNetDialog();
                        if (null != token
                                && !StringUtils.getInstance().isNullOrEmpty(token.getId())) {
                            createStripeTokenSuccess(token);
                        } else {
                            createStripeTokenFail();
                        }
                    }
                });
            } else {
                closeNetDialog();
                if (!card1.validateNumber()) {
                    PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, R.string.card_number_invalid);
                } else if (!card1.validateExpiryDate()) {
                    PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, R.string.card_expiry_date_invalid);
                } else if (!card1.validateCVC()) {
                    PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, R.string.card_cvv_invalid);
                } else {
                    PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, R.string.card_detail_invalid);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            PromptUtils.getInstance().showShortPromptToast(BasePayActivity.this, R.string.pay_fail);
            createStripeTokenFail();
            closeNetDialog();
        }
    }
}
