package com.mhysa.waimai.ui.activities.personal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.listview.XListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.db.CardLogicImp;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.model.card.Card;
import com.mhysa.waimai.ui.adapters.personal.CardListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/9/19
 *
 * @author xusheng
 */

public class CardListActivity extends BaseActivity {

    private static final int DELETE_SUCCESS = 0;

    private static final int GET_ALL_SUCCESS = 1;

    public static final String INTENT_IS_CHOOSE = "intent_is_choose";

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Bind(R.id.lv_card)
    XListView mCardLv;

    private CardListAdapter mAdapter;

    private ArrayList<Card> mCards = new ArrayList<Card>();

    private boolean mIsChoose;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case DELETE_SUCCESS: {
                    int position = msg.arg1;
                    PromptUtils.getInstance().showShortPromptToast(CardListActivity.this,
                            R.string.delete_success);
                    mCards.remove(position);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
                case GET_ALL_SUCCESS: {
                    List<Card> cards = (List<Card>) msg.obj;
                    mCards.clear();
                    mCards.addAll(cards);
                    mAdapter.notifyDataSetChanged();
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
        return R.layout.activity_card_list;
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();

        if (null != getIntent()) {
            mIsChoose = getIntent().getBooleanExtra(INTENT_IS_CHOOSE, false);
        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);
        mCardLv.setPullLoadEnable(false);
        mCardLv.setPullRefreshEnable(false);

        mAdapter = new CardListAdapter(CardListActivity.this, mCards);

        mAdapter.setListener(new CardListAdapter.CardListListener() {
            @Override
            public void delete(final int position, Card card) {
                if (position >= 0 && null != card && !StringUtils.getInstance().isNullOrEmpty(card.id)) {
                    deleteCard(position, card);
                }

            }
        });

        mCardLv.setAdapter(mAdapter);

        mCardLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsChoose && null != mCards && mCards.size() >= position) {
                    Card card = mCards.get(position - 1);
                    Intent intent = new Intent();
                    intent.putExtra("creditCard", card);
                    setResult(RESULT_OK, intent);
                    CardListActivity.this.finish();
                }
            }
        });
    }

    private void deleteCard(final int position, final Card card) {
        AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(this, true);
        builder.setTitle(R.string.tip);
        builder.setMessage(R.string.confirm_delete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete(position, card);
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

    private void doDelete(final int position, final Card card) {
//        showNetDialog("");
//        OkHttpClientManager.getInstance().deleteCard(Utils.getUserId(CardListActivity.this),
//                Utils.getToken(CardListActivity.this), card.id, new WtNetWorkListener<JsonElement>() {
//                    @Override
//                    public void onSucess(RemoteReturnData<JsonElement> data) {
//                        PromptUtils.getInstance().showShortPromptToast(CardListActivity.this,
//                                R.string.delete_success);
//                        mCards.remove(position);
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(String status, String msg_cn, String msg_en) {
//                        responseError(msg_cn, msg_en);
//                    }
//
//                    @Override
//                    public void onFinished() {
//                        closeNetDialog();
//                    }
//                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                CardLogicImp.deleteCard(card.id, Utils.getUserId(CardListActivity.this));
                Message message = new Message();
                message.what = DELETE_SUCCESS;
                message.arg1 = position;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        getData();

        EventBus.getDefault().register(this);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Card> cards = CardLogicImp.getCardList(Utils.getUserId(CardListActivity.this));
                if (null != cards && cards.size() > 0) {
                    Message message = new Message();
                    message.what = GET_ALL_SUCCESS;
                    message.obj = cards;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
//        OkHttpClientManager.getInstance().getCardList(Utils.getUserId(this),
//                Utils.getToken(this),
//                new WtNetWorkListener<CardList>() {
//                    @Override
//                    public void onSucess(RemoteReturnData<CardList> data) {
//                        if (null != data
//                                && null != data.data
//                                && data.data.list.size() > 0) {
//                            mCards.clear();
//                            mCards.addAll(data.data.list);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onError(String status, String msg_cn, String msg_en) {
//                        responseError(msg_cn, msg_en);
//                        LoginActivity.loginError(CardListActivity.this, status);
//                    }
//
//                    @Override
//                    public void onFinished() {
//                        closeNetDialog();
//                        mCardLv.stopRefresh();
//                        mCardLv.stopLoadMore();
//                    }
//                });
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
            case EventID.ADD_CARD_SUCCESS: {
                getData();
                break;
            }
        }
    }

    @OnClick({R.id.layout_back, R.id.layout_add})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                CardListActivity.this.finish();
                break;
            }

            case R.id.layout_add: {
                AddCardActivity.invoke(CardListActivity.this);
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

    public static void invoke(Context context, boolean isChoose) {
        Intent intent = new Intent(context, CardListActivity.class);
        intent.putExtra(INTENT_IS_CHOOSE, isChoose);
        context.startActivity(intent);
    }
}
