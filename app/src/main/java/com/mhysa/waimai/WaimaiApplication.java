package com.mhysa.waimai;

import com.joey.devilfish.application.BaseApplication;
import com.joey.devilfish.fusion.SharedPreferenceConstant;
import com.mhysa.waimai.db.SQLHelper;

/**
 * 文件描述
 * Date: 2017/7/20
 *
 * @author xusheng
 */

public class WaimaiApplication extends BaseApplication {

    private static WaimaiApplication sInstance;

    public static WaimaiApplication getInstance() {
        return sInstance;
    }

    private SQLHelper mSqlHelper;

    public SQLHelper getSQLHelper() {
        if (null == mSqlHelper) {
            mSqlHelper = new SQLHelper(this);
        }

        return mSqlHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        SharedPreferenceConstant.PREFERENCE_NAME = "Mhysa_Waimai";
    }

    @Override
    public void onTerminate() {
        closeSqlHelper();
        super.onTerminate();
    }

    private void closeSqlHelper() {
        if (null != mSqlHelper) {
            mSqlHelper.close();
            mSqlHelper = null;
        }
    }
}
