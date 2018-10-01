package com.mhysa.waimai.manager;

import android.os.Handler;
import android.os.Message;

import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.db.CartLogicImp;
import com.mhysa.waimai.model.cart.CartFood;

import java.util.HashMap;
import java.util.List;

/**
 * 购物车管理类
 * Date: 2017/8/3
 *
 * @author xusheng
 */

public class CartManager {

    // 添加商品
    public static final int ADD_FOOD_TO_CART_SUCCESS = 10001;

    // 删除商品
    public static final int DELETE_FOOD_SUCCESS = 10002;

    // 获取某店铺的包含在购物车中的商品
    public static final int GET_FOODS_BY_STORE_SUCCESS = 10003;

    // 获取购物车中所有的商品
    public static final int GET_ALL_FOOD_SUCCESS = 10004;

    // 获取购物车中所有的商品
    public static final int GET_ALL_FOOD_SORT_BY_STORE_SUCCESS = 10005;

    // 清空购物车成功
    public static final int CLEAR_CART_SUCCESS = 10006;

    // 删除符合条件的所有商品成功
    public static final int DELETE_ALL_TARGET_FOOD_SUCCESS = 10007;

    // 获取购物车中某个商家某个商品的数量
    public static final int GET_COUNT_SORT_BY_FOOD_SUCCESS = 10008;

    private static CartManager mInstance;

    private CartManager() {
    }

    public static CartManager getInstance() {
        if (mInstance == null) {
            synchronized (CartManager.class) {
                if (mInstance == null) {
                    mInstance = new CartManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取购物车中所有的商品
     *
     * @param handler
     */
    public void getAllFoodsSortByStore(final Handler handler) {
        if (null == handler) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, List<CartFood>> cartFoodHashMap = CartLogicImp.getAllFoodSortByStore();
                Message message = new Message();
                message.what = GET_ALL_FOOD_SORT_BY_STORE_SUCCESS;
                message.obj = cartFoodHashMap;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 获取所有的购物车商品
     */
    public void getAllFoods(final Handler handler) {
        if (null == handler) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CartFood> foods = CartLogicImp.getAllFoods();
                Message message = new Message();
                message.what = GET_ALL_FOOD_SUCCESS;
                message.obj = foods;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 请求购物车中某商家的商品
     *
     * @param storeId
     */
    public void getCartFoodByStore(final Handler handler, final String storeId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, List<CartFood>> map = CartLogicImp.getCartFoodByStore(storeId);
                Message message = new Message();
                message.what = GET_FOODS_BY_STORE_SUCCESS;
                message.obj = map;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 添加商品到购物车
     *
     * @param cartFood
     */
    public void addFood(final CartFood cartFood, final Handler handler) {
        if (null == handler
                || null == cartFood
                || StringUtils.getInstance().isNullOrEmpty(cartFood.foods_id)
                || StringUtils.getInstance().isNullOrEmpty(cartFood.store_id)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                CartLogicImp.addFood(cartFood);

                Message message = new Message();
                message.what = ADD_FOOD_TO_CART_SUCCESS;
                message.obj = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 添加商品到购物车
     *
     * @param cartFoods
     * @param handler
     */
    public void addFoods(final List<CartFood> cartFoods, final Handler handler) {
        if (null == handler
                || null == cartFoods
                || cartFoods.size() == 0) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int number = 0;

                final int size = cartFoods.size();
                for (int i = 0; i < size; i++) {
                    CartFood cartFood = cartFoods.get(i);
                    if (null != cartFood
                            && !StringUtils.getInstance().isNullOrEmpty(cartFood.foods_id)
                            && !StringUtils.getInstance().isNullOrEmpty(cartFood.store_id)) {
                        CartLogicImp.addFood(cartFood);
                        number++;
                    }
                }

                if (number > 0) {
                    Message message = new Message();
                    message.what = ADD_FOOD_TO_CART_SUCCESS;
                    message.obj = number;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 删除商品
     *
     * @param foodId
     * @param storeId
     * @param specialId
     */
    public void deleteFood(final Handler handler, final String foodId,
                           final String storeId, final String specialId) {
        if (null == handler
                || StringUtils.getInstance().isNullOrEmpty(foodId)
                || StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                CartLogicImp.deleteFood(foodId, storeId, specialId);
                handler.sendEmptyMessage(DELETE_FOOD_SUCCESS);
            }
        }).start();
    }

    /**
     * 删除购物车
     */
    public void clearCartTable(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CartLogicImp.clearCartTable();
                if (null != handler) {
                    handler.sendEmptyMessage(CLEAR_CART_SUCCESS);
                }
            }
        }).start();
    }

    public void deleteAllTargetFood(final Handler handler, final String foodId,
                                    final String storeId, final String specialId) {
        if (null == handler
                || StringUtils.getInstance().isNullOrEmpty(foodId)
                || StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                CartLogicImp.deleteAllTargetFood(foodId, storeId, specialId);
                handler.sendEmptyMessage(DELETE_ALL_TARGET_FOOD_SUCCESS);
            }
        }).start();
    }

    public void getCountByFoodId(final String foodId, final String storeId,
                                 final Handler handler) {
        if (null == handler
                || StringUtils.getInstance().isNullOrEmpty(foodId)
                || StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int number = CartLogicImp.getCountByFoodId(foodId, storeId);
                Message message = new Message();
                message.what = GET_COUNT_SORT_BY_FOOD_SUCCESS;
                message.obj = number;
                handler.sendMessage(message);
            }
        }).start();
    }

    public void deleteByStoreId(final String storeId) {
        if (StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                CartLogicImp.deleteByStoreId(storeId);
            }
        }).start();
    }
}
