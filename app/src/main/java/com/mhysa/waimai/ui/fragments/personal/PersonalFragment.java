package com.mhysa.waimai.ui.fragments.personal;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.joey.devilfish.ui.fragment.base.BaseFragment;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.StringUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.event.ApplicationEvent;
import com.mhysa.waimai.event.EventID;
import com.mhysa.waimai.ui.activities.account.MyWalletActivity;
import com.mhysa.waimai.ui.activities.message.MessageListActivity;
import com.mhysa.waimai.ui.activities.order.OrderListActivity;
import com.mhysa.waimai.ui.activities.personal.AddressListActivity;
import com.mhysa.waimai.ui.activities.personal.CardListActivity;
import com.mhysa.waimai.ui.activities.personal.ContactUsActivity;
import com.mhysa.waimai.ui.activities.personal.PersonalSettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 文件描述
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class PersonalFragment extends BaseFragment {

    @Bind(R.id.scrollview)
    PullToRefreshScrollView mScrollView;

    @Bind(R.id.tv_name)
    TextView mNameTv;

    @Bind(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @Bind(R.id.layout_header)
    RelativeLayout mHeaderLayout;

    @Bind(R.id.view_divider)
    View mViewDivider;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void initContentView() {
        super.initContentView();

        mScrollView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ApplicationEvent event) {
        final int eventId = event.getEventId();
        switch (eventId) {
            case EventID.CHANGE_NICKNAME_SUCCESS: {
                if (null != event.getEventMessage()) {
                    String nickname = event.getEventMessage();
                    Utils.setNickName(mContext, nickname);
                    mNameTv.setText(nickname);
                }
                break;
            }

            case EventID.CHANGE_AVATAR_SUCCESS: {
                if (null != event.getEventMessage()) {
                    String path = event.getEventMessage();
                    Utils.setHeadImage(mContext, path);
                    ImageUtils.getInstance().setImageURL(path, mAvatarSdv);
                }
                break;
            }
        }
    }

    @OnClick({R.id.layout_setting, R.id.layout_my_wallet,
            R.id.layout_address, R.id.layout_message,
            R.id.layout_card, R.id.layout_order_list,
            R.id.layout_contact_us})
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.layout_setting: {
                PersonalSettingActivity.invoke(getActivity());
                break;
            }

            case R.id.layout_my_wallet: {
                MyWalletActivity.invoke(getActivity());
                break;
            }

            case R.id.layout_address: {
                AddressListActivity.invoke(getActivity(), false);
                break;
            }

            case R.id.layout_message: {
                MessageListActivity.invoke(getActivity());
                break;
            }

            case R.id.layout_card: {
                CardListActivity.invoke(getActivity(), false);
                break;
            }

            case R.id.layout_order_list: {
                OrderListActivity.invoke(getActivity(), mContext.getResources().getString(R.string.history_order));
                break;
            }

            case R.id.layout_contact_us: {
                ContactUsActivity.invoke(getActivity());
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!StringUtils.getInstance().isNullOrEmpty(Utils.getNickName(mContext))) {
            mNameTv.setText(Utils.getNickName(mContext));
        } else {
            mNameTv.setText("");
        }

        if (!StringUtils.getInstance().isNullOrEmpty(Utils.getHeadImage(mContext))) {
            ImageUtils.getInstance().setImageURL(Utils.getHeadImage(mContext), mAvatarSdv);
        } else {
            ImageUtils.getInstance().setImageURL("", mAvatarSdv);
        }

        mHeaderLayout.getBackground().setAlpha(255);
        mViewDivider.getBackground().setAlpha(255);
    }
}
