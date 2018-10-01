package com.mhysa.waimai.ui.activities.launch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.fusion.FusionCode;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ImageUtils;
import com.joey.devilfish.utils.Utils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.http.OkHttpClientManager;
import com.mhysa.waimai.http.RemoteReturnData;
import com.mhysa.waimai.http.WtNetWorkListener;
import com.mhysa.waimai.model.banner.Banners;
import com.mhysa.waimai.ui.activities.main.MainActivity;
import com.mhysa.waimai.ui.activities.slide.SlideActivity;

/**
 * 启动页
 * Date: 2017/8/25
 *
 * @author xusheng
 */

public class LaunchActivity extends BaseActivity {

    private static final int ENTER_MAIN = 0;

    private static final int ENTER_SLIDE = 1;

    private static final int ENTER_APP = 2;

    private static final int DELAY = 2000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case ENTER_MAIN: {
                    gotoMain();
                    break;
                }

                case ENTER_SLIDE: {
                    Banners banners = (Banners) msg.obj;
                    gotoSlide(banners);
                    break;
                }

                case ENTER_APP: {
                    if (Utils.getIsFirst(LaunchActivity.this)) {
                        Utils.setIsFirst(LaunchActivity.this, false);
                        SlideActivity.invoke(LaunchActivity.this);
                        LaunchActivity.this.finish();
                        overridePendingTransition(R.anim.activity_translate_right_in,
                                R.anim.activity_translate_right_out);
                    } else {
                        MainActivity.invoke(LaunchActivity.this);
                        LaunchActivity.this.finish();
                        overridePendingTransition(R.anim.activity_translate_right_in,
                                R.anim.activity_translate_right_out);
                    }
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
        return R.layout.activity_launch;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
//        if (!NetworkUtils.isNetworkAvailable(this)
//                || !Utils.getIsFirst(this)) {
//            mHandler.sendEmptyMessageDelayed(ENTER_MAIN, DELAY);
//        } else {
//            getBanners();
//        }

        mHandler.sendEmptyMessageDelayed(ENTER_APP, DELAY);
    }

    private void gotoMain() {
        MainActivity.invoke(this);
        LaunchActivity.this.finish();
        overridePendingTransition(R.anim.activity_translate_right_in,
                R.anim.activity_translate_right_out);
    }

    private void getBanners() {
        OkHttpClientManager.getInstance().getBanners(FusionCode.BannerType.BANNER_TYPE_SLIDE,
                new WtNetWorkListener<Banners>() {
                    @Override
                    public void onSucess(RemoteReturnData<Banners> data) {
                        if (null != data
                                && null != data.data
                                && null != data.data.rows
                                && data.data.rows.size() > 0) {
                            Message message = new Message();
                            message.what = ENTER_SLIDE;
                            message.obj = data.data;
                            mHandler.sendMessageDelayed(message, 3000);
                            // 缓存图片
                            final int size = data.data.rows.size();
                            for (int i = 0; i < size; i++) {
                                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(LaunchActivity.this);
                                ImageUtils.getInstance().setImageURL(data.data.rows.get(i).image, simpleDraweeView);
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mHandler.sendEmptyMessageDelayed(ENTER_MAIN, DELAY);
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(String status, String msg_cn, String msg_en) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.sendEmptyMessageDelayed(ENTER_MAIN, DELAY);
                            }
                        });
                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    private void gotoSlide(Banners banners) {
        Utils.setIsFirst(this, false);
        SlideActivity.invoke(LaunchActivity.this, banners);
        LaunchActivity.this.finish();
        overridePendingTransition(R.anim.activity_translate_right_in,
                R.anim.activity_translate_right_out);
    }
}
