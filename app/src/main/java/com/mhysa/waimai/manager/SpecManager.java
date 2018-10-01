package com.mhysa.waimai.manager;

import android.app.Dialog;
import android.content.Context;

import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.special.SpecialClass;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.ui.customerviews.spec.SpecChooseLayout;
import com.mhysa.waimai.utils.PromptDialogBuilder;

import java.util.List;

/**
 * 商品规格
 * Date: 2017/8/4
 *
 * @author xusheng
 */

public class SpecManager {

    private static SpecManager mInstance;

    private Dialog mDialog;

    private SpecManager() {
    }

    public static SpecManager getInstance() {
        if (mInstance == null) {
            synchronized (SpecManager.class) {
                if (mInstance == null) {
                    mInstance = new SpecManager();
                }
            }
        }
        return mInstance;
    }

    public void showSpecDialog(Context context, List<SpecialClass> spec_class_list,
                               final Food food, final Store store, final OnSpecChooseListener listener) {
        PromptDialogBuilder builder = new PromptDialogBuilder(context);
        builder.setIsHasBackground(false);

        SpecChooseLayout specChooseLayout = new SpecChooseLayout(context);
        specChooseLayout.setListener(new SpecChooseLayout.OnConfirmListener() {
            @Override
            public void onConfirm(String ids, String specials_name_cn,
                                  String specials_name_en, int number,
                                  String price) {
                if (null != listener) {
                    listener.onSpecChoose(ids, specials_name_cn,
                            specials_name_en, number, food, store, price);
                }
                if (null != mDialog) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancel() {
                if (null != listener) {
                    listener.onCancel();
                }

                if (null != mDialog) {
                    mDialog.dismiss();
                }
            }
        });
        specChooseLayout.setData(food, store, spec_class_list);
        builder.setView(specChooseLayout);
        mDialog = builder.create();
        mDialog.show();
    }

    public interface OnSpecChooseListener {

        void onSpecChoose(String ids, String specials_name_cn, String specials_name_en,
                          int count, Food food, Store store, String price);

        void onCancel();
    }
}
