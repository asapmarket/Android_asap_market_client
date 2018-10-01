package com.mhysa.waimai.ui.activities.base;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.joey.devilfish.ui.activity.base.BaseActivity;
import com.joey.devilfish.utils.PromptUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.manager.TakePictureManager;
import com.mhysa.waimai.ui.fragments.bottommenu.BottomMenuFragment;

import java.io.File;
import java.util.List;

/**
 * 带有选择图片和拍照功能的activity基类
 * Date: 2018/1/17
 *
 * @author xusheng
 */

public abstract class BaseUploadImageActivity extends BaseActivity {

    private TakePictureManager mTakePictureManager;

    public abstract void selectPhotoSuccess(Uri uri);

    public void showMenu(final int aspectX, final int aspectY, final int outputX, final int outputY) {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        bottomMenuFragment.setListener(new BottomMenuFragment.OnMenuSelectedListener() {
            @Override
            public void onTakePhoto() {
                // 拍照
                takePhoto(aspectX, aspectY, outputX, outputY);
            }

            @Override
            public void onSelectPhoto() {
                // 选择本地图片
                selectPhoto(aspectX, aspectY, outputX, outputY);
            }
        });

        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void takePhoto(int aspectX, int aspectY, int outputX, int outputY) {
        mTakePictureManager = new TakePictureManager(this);
        if (aspectX != 0 && aspectY != 0 && outputX != 0 && outputY != 0) {
            //开启裁剪 比例  宽高  (默认不裁剪)
            mTakePictureManager.setTailor(aspectX, aspectY, outputX, outputY);
        }
        //拍照方式
        mTakePictureManager.startTakeWayByCarema();
        //回调
        mTakePictureManager.setTakePictureCallBackListener(new TakePictureManager.takePictureCallBackListener() {
            //成功拿到图片,isTailor 是否裁剪？ ,outFile 拿到的文件 ,filePath拿到的URl
            @Override
            public void successful(boolean isTailor, File outFile, Uri filePath) {
                selectPhotoSuccess(filePath);
            }

            //失败回调
            @Override
            public void failed(int errorCode, List<String> deniedPermissions) {
                PromptUtils.getInstance().showShortPromptToast(BaseUploadImageActivity.this, R.string.select_error);
            }
        });
    }

    private void selectPhoto(int aspectX, int aspectY, int outputX, int outputY) {
        mTakePictureManager = new TakePictureManager(this);
        if (aspectX != 0 && aspectY != 0 && outputX != 0 && outputY != 0) {
            //开启裁剪 比例  宽高  (默认不裁剪)
            mTakePictureManager.setTailor(aspectX, aspectY, outputX, outputY);
        }
        mTakePictureManager.startTakeWayByAlbum();
        mTakePictureManager.setTakePictureCallBackListener(new TakePictureManager.takePictureCallBackListener() {
            @Override
            public void successful(boolean isTailor, File outFile, Uri filePath) {
                selectPhotoSuccess(filePath);
            }

            @Override
            public void failed(int errorCode, List<String> deniedPermissions) {
                PromptUtils.getInstance().showShortPromptToast(BaseUploadImageActivity.this, R.string.select_error);
            }

        });
    }

    /**
     * 把本地的onActivityResult()方法回调绑定到对象
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            mTakePictureManager.attachToActivityForResult(requestCode, resultCode, data);

        }
    }


    /**
     * onRequestPermissionsResult()方法权限回调绑定到对象
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mTakePictureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
