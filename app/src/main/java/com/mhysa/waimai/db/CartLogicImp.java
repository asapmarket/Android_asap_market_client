package com.mhysa.waimai.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.WaimaiApplication;
import com.mhysa.waimai.model.cart.CartFood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 购物车相关实现类
 * Date: 2017/8/3
 *
 * @author xusheng
 */

public class CartLogicImp {

    /**
     * 获取所有的购物车商品
     */
    public static List<CartFood> getAllFoods() {
        List<CartFood> foods = new ArrayList<CartFood>();

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getReadableDatabase();
            cursor = database.query(false, SQLHelper.TABLE_CART, null, null, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String foods_id = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_ID));
                CartFood cartFood = new CartFood();
                cartFood.foods_id = foods_id;
                cartFood.foods_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_CN));
                cartFood.foods_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_EN));
                cartFood.foods_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_CN));
                cartFood.foods_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_EN));
                cartFood.price = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_PRICE));
                cartFood.spec_id_list = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_ID));
                cartFood.specials_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_CN));
                cartFood.specials_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_EN));
                cartFood.store_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_EN));
                cartFood.store_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_CN));
                cartFood.store_id = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_ID));
                cartFood.store_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_CN));
                cartFood.store_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_EN));

                foods.add(cartFood);
            }
        } catch (Exception exception) {

        } finally {
            if (database != null) {
                database.close();
            }
        }

        return foods;
    }

    /**
     * 获取所有的购物车，按照商家排序
     *
     * @return
     */
    public static HashMap<String, List<CartFood>> getAllFoodSortByStore() {

        HashMap<String, List<CartFood>> cartFoodHashMap = new HashMap<String, List<CartFood>>();

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getReadableDatabase();
            cursor = database.query(false, SQLHelper.TABLE_CART, null, null, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String storeId = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_ID));
                CartFood cartFood = new CartFood();
                cartFood.foods_id = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_ID));
                cartFood.foods_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_CN));
                cartFood.foods_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_EN));
                cartFood.foods_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_CN));
                cartFood.foods_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_EN));
                cartFood.price = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_PRICE));
                cartFood.spec_id_list = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_ID));
                cartFood.specials_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_CN));
                cartFood.specials_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_EN));
                cartFood.store_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_EN));
                cartFood.store_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_CN));
                cartFood.store_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_CN));
                cartFood.store_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_EN));
                cartFood.store_id = storeId;

                if (cartFoodHashMap.containsKey(storeId)) {
                    List<CartFood> foods = cartFoodHashMap.get(storeId);
                    foods.add(cartFood);

                    cartFoodHashMap.put(storeId, foods);
                } else {
                    List<CartFood> foods = new ArrayList<CartFood>();
                    foods.add(cartFood);

                    cartFoodHashMap.put(storeId, foods);
                }
            }
        } catch (Exception exception) {

        } finally {
            if (database != null) {
                database.close();
            }
        }

        return cartFoodHashMap;
    }

    /**
     * 请求购物车中某商家的商品
     *
     * @param storeId
     * @return String是商品的id
     */
    public static HashMap<String, List<CartFood>> getCartFoodByStore(String storeId) {

        HashMap<String, List<CartFood>> cartFoodHashMap = new HashMap<String, List<CartFood>>();

        String selection = SQLHelper.STORE_ID + "=? ";
        String[] selectionArgs = new String[]{storeId};

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getReadableDatabase();
            cursor = database.query(false, SQLHelper.TABLE_CART, null, selection, selectionArgs,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String foods_id = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_ID));
                CartFood cartFood = new CartFood();
                cartFood.foods_id = foods_id;
                cartFood.foods_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_CN));
                cartFood.foods_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_NAME_EN));
                cartFood.foods_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_CN));
                cartFood.foods_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_IMAGE_EN));
                cartFood.price = cursor.getString(cursor.getColumnIndex(SQLHelper.FOODS_PRICE));
                cartFood.spec_id_list = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_ID));
                cartFood.specials_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_CN));
                cartFood.specials_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.SPECIALS_NAME_EN));
                cartFood.store_name_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_EN));
                cartFood.store_name_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_NAME_CN));
                cartFood.store_id = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_ID));
                cartFood.store_image_cn = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_CN));
                cartFood.store_image_en = cursor.getString(cursor.getColumnIndex(SQLHelper.STORE_IMAGE_EN));

                if (cartFoodHashMap.containsKey(foods_id)) {
                    List<CartFood> foods = cartFoodHashMap.get(foods_id);
                    foods.add(cartFood);

                    cartFoodHashMap.put(foods_id, foods);
                } else {
                    List<CartFood> foods = new ArrayList<CartFood>();
                    foods.add(cartFood);

                    cartFoodHashMap.put(foods_id, foods);
                }
            }
        } catch (Exception exception) {

        } finally {
            if (database != null) {
                database.close();
            }
        }

        return cartFoodHashMap;
    }

    /**
     * 添加商品到购物车
     *
     * @param cartFood
     */
    public static void addFood(CartFood cartFood) {
        SQLiteDatabase database = null;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(SQLHelper.FOODS_ID, cartFood.foods_id);
            values.put(SQLHelper.FOODS_NAME_CN, cartFood.foods_name_cn);
            values.put(SQLHelper.FOODS_NAME_EN, cartFood.foods_name_en);
            values.put(SQLHelper.FOODS_IMAGE_CN, cartFood.foods_image_cn);
            values.put(SQLHelper.FOODS_IMAGE_EN, cartFood.foods_image_en);
            values.put(SQLHelper.FOODS_PRICE, cartFood.price);
            values.put(SQLHelper.SPECIALS_ID, cartFood.spec_id_list);
            values.put(SQLHelper.SPECIALS_NAME_CN, cartFood.specials_name_cn);
            values.put(SQLHelper.SPECIALS_NAME_EN, cartFood.specials_name_en);
            values.put(SQLHelper.STORE_NAME_CN, cartFood.store_name_cn);
            values.put(SQLHelper.STORE_NAME_EN, cartFood.store_name_en);
            values.put(SQLHelper.STORE_ID, cartFood.store_id);
            values.put(SQLHelper.STORE_IMAGE_CN, cartFood.store_image_cn);
            values.put(SQLHelper.STORE_IMAGE_EN, cartFood.store_image_en);

            database.insert(SQLHelper.TABLE_CART, null, values);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 删除商品，符合条件的某一条
     *
     * @param foodId
     * @param storeId
     * @param specialId
     */
    public static void deleteFood(String foodId, String storeId,
                                  String specialId) {

        SQLiteDatabase database = null;
        String selection = "";
        String[] selectionArgs;

        if (StringUtils.getInstance().isNullOrEmpty(specialId)) {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId};
        } else {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? AND " + SQLHelper.SPECIALS_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId, specialId};
        }

        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();
            Integer[] integer = getExistNumber(foodId, storeId, specialId, database);

            if (integer[0] == 1) {
                database.delete(SQLHelper.TABLE_CART, selection, selectionArgs);
            } else if (integer[0] > 1) {
                String deleteSelection = SQLHelper.ID + "=? ";
                String[] deleteSelectionArgs = new String[]{integer[1] + ""};
                database.delete(SQLHelper.TABLE_CART, deleteSelection, deleteSelectionArgs);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }

    }

    /**
     * 判断是否已经存在
     *
     * @param foodId
     * @param storeId
     * @param specialId
     * @param database
     * @return
     */
    private static Integer[] getExistNumber(String foodId, String storeId,
                                            String specialId,
                                            SQLiteDatabase database) {
        int existNumber = 0;
        Cursor cursor = null;
        String selection = "";
        String[] selectionArgs;
        Integer[] integer = new Integer[2];

        if (StringUtils.getInstance().isNullOrEmpty(specialId)) {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId};
        } else {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? AND " + SQLHelper.SPECIALS_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId, specialId};
        }

        try {
            cursor = database.query(false, SQLHelper.TABLE_CART, null, selection, selectionArgs,
                    null, null, null, null);
            existNumber = cursor.getCount();
            if (null != cursor && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(SQLHelper.ID));
                integer = new Integer[]{existNumber, id};
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

        }

        return integer;
    }

    /**
     * 清空购物车
     */
    public static void clearCartTable() {
        String sql = "DELETE FROM " + SQLHelper.TABLE_CART;
        SQLiteDatabase db = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * 删除符合条件的所有数据
     *
     * @param storeId
     * @param foodId
     * @param specialId
     */
    public static void deleteAllTargetFood(String foodId, String storeId, String specialId) {
        SQLiteDatabase database = null;
        String selection = "";
        String[] selectionArgs;

        if (StringUtils.getInstance().isNullOrEmpty(specialId)) {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId};
        } else {
            selection = SQLHelper.FOODS_ID + "=? AND " +
                    SQLHelper.STORE_ID + "=? AND " + SQLHelper.SPECIALS_ID + "=? ";
            selectionArgs = new String[]{foodId, storeId, specialId};
        }

        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();
            database.delete(SQLHelper.TABLE_CART, selection, selectionArgs);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    public static int getCountByFoodId(String foodId, String storeId) {
        String selection = SQLHelper.STORE_ID + "=? AND " + SQLHelper.FOODS_ID + "=? ";
        String[] selectionArgs = new String[]{storeId, foodId};

        SQLiteDatabase database = null;
        Cursor cursor = null;
        int number = 0;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getReadableDatabase();
            cursor = database.query(false, SQLHelper.TABLE_CART, null, selection, selectionArgs,
                    null, null, null, null);
            number = cursor.getCount();
        } catch (Exception exception) {

        } finally {
            if (database != null) {
                database.close();
            }
        }

        return number;
    }

    public static void deleteByStoreId(String storeId) {
        if (StringUtils.getInstance().isNullOrEmpty(storeId)) {
            return;
        }
        SQLiteDatabase database = null;
        String selection = SQLHelper.STORE_ID + "=? ";
        String[] selectionArgs = new String[]{storeId};

        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();
            database.delete(SQLHelper.TABLE_CART, selection, selectionArgs);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }
}
