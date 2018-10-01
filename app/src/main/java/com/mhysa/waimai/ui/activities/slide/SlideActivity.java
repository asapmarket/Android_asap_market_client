package com.mhysa.waimai.ui.activities.slide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.ImageUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.banner.Banners;
import com.mhysa.waimai.ui.activities.main.MainActivity;
import com.mhysa.waimai.ui.adapters.slide.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * 文件描述
 * Date: 2017/8/25
 *
 * @author xusheng
 */

public class SlideActivity extends BaseActivity {

    private static final String INTENT_BANNERS = "intent_banners";

    private ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;

    private ArrayList<View> mViews = new ArrayList<View>();

    private int mCurrentPosition = 0;

    private Banners mBanners;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_slide;
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();
//        if (null != getIntent()) {
//            mBanners = (Banners) getIntent().getSerializableExtra(INTENT_BANNERS);
//        }
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        super.initContentView(savedInstanceState);

        mViewPager = (ViewPager) this.findViewById(R.id.viewpager);

//        List<Banner> banners = mBanners.rows;
//        final int size = banners.size();
//        for (int i = 0; i < size; i++) {
//            View view = View.inflate(SlideActivity.this, R.layout.list_item_slide, null);
//            SimpleDraweeView iconSdv = (SimpleDraweeView) view.findViewById(R.id.sdv_icon);
//            ImageUtils.getInstance().setImageURL(banners.get(i).image, iconSdv);
//
//            mViews.add(view);
//        }

        for (int i = 0; i < 3; i++) {
            View view = View.inflate(SlideActivity.this, R.layout.list_item_slide, null);
            SimpleDraweeView iconSdv = (SimpleDraweeView) view.findViewById(R.id.sdv_icon);
            if (i == 0) {
                ImageUtils.getInstance().setImageResId(R.mipmap.ic_slide_first, iconSdv);
            } else if (i == 1) {
                ImageUtils.getInstance().setImageResId(R.mipmap.ic_slide_second, iconSdv);
            } else if (i == 2) {
                ImageUtils.getInstance().setImageResId(R.mipmap.ic_slide_third, iconSdv);
                ImageView enterIv = (ImageView) view.findViewById(R.id.iv_enter);
                enterIv.setVisibility(View.VISIBLE);
                enterIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterMain();
                    }
                });
            }


            mViews.add(view);
        }

        mAdapter = new ViewPagerAdapter(mViews);
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPosition = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            float startX;
//            float startY;
//            float endX;
//            float endY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        startY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        endX = event.getX();
//                        endY = event.getY();
//                        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//                        //获取屏幕的宽度
//                        Point size = new Point();
//                        windowManager.getDefaultDisplay().getSize(size);
//                        int width = size.x;
//                        if (mCurrentPosition == (mViews.size() - 1)
//                                && startX - endX > 0
//                                && startX - endX >= (width / 4)) {
//                            enterMain();
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private void enterMain() {
        startActivity(new Intent(SlideActivity.this, MainActivity.class));
        SlideActivity.this.finish();
        overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
    }

    public static void invoke(Context context, Banners banners) {
        Intent intent = new Intent(context, SlideActivity.class);
        intent.putExtra(INTENT_BANNERS, banners);
        context.startActivity(intent);
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, SlideActivity.class);
        context.startActivity(intent);
    }
}
