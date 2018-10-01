package com.mhysa.waimai.ui.activities.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ExtendUtils;
import com.joey.devilfish.utils.NetworkUtils;
import com.joey.devilfish.utils.PromptUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.joey.devilfish.widget.CircularProgress;
import com.joey.devilfish.widget.listview.XListView;
import com.joey.devilfish.widget.swipemenulistview.SwipeMenu;
import com.joey.devilfish.widget.swipemenulistview.SwipeMenuCreator;
import com.joey.devilfish.widget.swipemenulistview.SwipeMenuItem;
import com.joey.devilfish.widget.swipemenulistview.SwipeMenuListView;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.message.Message;
import com.mhysa.waimai.model.message.MessageList;
import com.mhysa.waimai.ui.activities.login.LoginActivity;
import com.mhysa.waimai.ui.adapters.message.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 会员消息列表
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class MessageListActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.lv_message)
    SwipeMenuListView mMessageLv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    private MessageListAdapter mAdapter;

    private ArrayList<Message> mMessages = new ArrayList<Message>();

    private int mCurrentPage = FusionCode.PageConstant.INIT_PAGE;

    private View mFooterView;

    private TextView mLoadMoreTv;

    private CircularProgress mLoadMoreProgress;

    private int mLastVisibleIndex;

    private boolean mIsLoadMore = false;

    private boolean mIsRefresh = false;

    private boolean mHasFooterView = false;

    @Override
    protected void initHeaderView(Bundle savedInstanceState) {
        super.initHeaderView(savedInstanceState);
        mTitleTv.setText(R.string.message);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mFooterView = LayoutInflater.from(this).inflate(R.layout.layout_load_more, null, false);
        mLoadMoreTv = (TextView) mFooterView.findViewById(R.id.tv_footer_text);
        mLoadMoreProgress = (CircularProgress) mFooterView.findViewById(R.id.progress);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });

        mMessageLv.setPullLoadEnable(false);
        mMessageLv.setPullRefreshEnable(true);

        mMessageLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && mLastVisibleIndex >= mAdapter.getCount() - 1) {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MessageListActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setWidth(ExtendUtils.getInstance().dip2px(MessageListActivity.this, 90));
                menu.addMenuItem(deleteItem);
            }
        };

        mMessageLv.setMenuCreator(creator);
        mMessageLv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                delete(position);
            }
        });

        mMessageLv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {

            }
        });

        mAdapter = new MessageListAdapter(MessageListActivity.this, mMessages);
        mMessageLv.setAdapter(mAdapter);
    }

    private void delete(final int position) {
        if (null != mMessages && mMessages.size() > position) {
            AlertDialog.Builder builder = PromptUtils.getInstance().getAlertDialog(MessageListActivity.this, true);
            builder.setMessage(R.string.confirm_delete)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doDelete(position);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }
    }

    private void doDelete(int position) {
        Message message = mMessages.get(position);
        if (null != message
                && !StringUtils.getInstance().isNullOrEmpty(message.message_id)) {
            showNetDialog("");
            OkHttpClientManager.getInstance().deleteMessage(Utils.getUserId(this),
                    Utils.getToken(this), message.message_id, new WtNetWorkListener<JsonElement>() {
                        @Override
                        public void onSucess(RemoteReturnData<JsonElement> data) {
                            PromptUtils.getInstance().showShortPromptToast(MessageListActivity.this,
                                    R.string.delete_success);
                        }

                        @Override
                        public void onError(String status, String msg_cn, String msg_en) {
                            responseError(msg_cn, msg_en);
                            LoginActivity.loginError(MessageListActivity.this, status);
                        }

                        @Override
                        public void onFinished() {
                            closeNetDialog();
                        }
                    });
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        showNetDialog("");
        getDatas();
    }

    private void refresh() {
        if (mIsRefresh) {
            return;
        }

        if (mIsLoadMore) {
            mMessageLv.stopRefresh();
            return;
        }

        mIsRefresh = true;
        mCurrentPage = FusionCode.PageConstant.INIT_PAGE;
        getDatas();
    }

    private void loadMore() {
        if (mIsLoadMore) {
            return;
        }

        if (mIsRefresh) {
            mLoadMoreProgress.stopSpinning();
            mLoadMoreProgress.setVisibility(View.GONE);
            mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
            return;
        }

        mIsLoadMore = true;

        if (!NetworkUtils.isNetworkAvailable(MessageListActivity.this)) {
            mLoadMoreProgress.setVisibility(View.VISIBLE);
            mLoadMoreProgress.startSpinning();
            mLoadMoreTv.setText(R.string.xlistview_header_hint_loading);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mLoadMoreProgress.stopSpinning();
                    mLoadMoreProgress.setVisibility(View.GONE);
                    mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
                    mIsLoadMore = false;
                }
            }, 100);
            return;
        }

        mLoadMoreProgress.setVisibility(View.VISIBLE);
        mLoadMoreProgress.startSpinning();
        mLoadMoreTv.setText(R.string.xlistview_header_hint_loading);

        getDatas();
    }

    private void getDatas() {
        OkHttpClientManager.getInstance().getMessages(Utils.getUserId(MessageListActivity.this),
                Utils.getToken(MessageListActivity.this), mCurrentPage, FusionCode.PageConstant.PAGE_SIZE,
                new WtNetWorkListener<MessageList>() {
                    @Override
                    public void onSucess(RemoteReturnData<MessageList> data) {
                        if (null != data && null != data.data
                                && null != data.data.rows && data.data.rows.size() > 0) {
                            List<Message> messages = data.data.rows;
                            int page = data.data.total_page;
                            final int size = messages.size();

                            if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE) {
                                mMessages.clear();
                            }

                            if (size < FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage > FusionCode.PageConstant.INIT_PAGE
                                    && mHasFooterView) {
                                mMessageLv.removeFooterView(mFooterView);
                                mHasFooterView = false;
                            }

                            if (size == FusionCode.PageConstant.PAGE_SIZE
                                    && mCurrentPage < page) {
                                if (mCurrentPage == FusionCode.PageConstant.INIT_PAGE && !mHasFooterView) {
                                    mMessageLv.addFooterView(mFooterView);
                                    mHasFooterView = true;
                                }

                                mCurrentPage = mCurrentPage + 1;
                            }
                            mMessages.addAll(messages);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            if (mCurrentPage != FusionCode.PageConstant.INIT_PAGE && mHasFooterView) {
                                mMessageLv.removeFooterView(mFooterView);
                                mHasFooterView = false;
                            }
                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        responseError(msg_cn, msg_en);
                        LoginActivity.loginError(MessageListActivity.this, status);
                    }

                    @Override
                    public void onFinished() {
                        mIsRefresh = false;
                        mIsLoadMore = false;
                        closeNetDialog();
                        mMessageLv.stopLoadMore();
                        mMessageLv.stopRefresh();
                        mLoadMoreProgress.stopSpinning();
                        mLoadMoreProgress.setVisibility(View.GONE);
                        mLoadMoreTv.setText(R.string.xlistview_footer_hint_normal);
                    }
                });
    }

    @OnClick({R.id.layout_back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.layout_back: {
                MessageListActivity.this.finish();
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

    public static void invoke(Context context) {
        Intent intent = new Intent(context, MessageListActivity.class);
        context.startActivity(intent);
    }
}
