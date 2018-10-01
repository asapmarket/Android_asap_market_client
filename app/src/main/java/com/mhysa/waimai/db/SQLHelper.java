package com.mhysa.waimai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * Date: 2017/8/3
 *
 * @author xusheng
 */

public class SQLHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "waimai.db";

    public static final int VERSION = 2;

    public static final String TABLE_CART = "cart";

    public static final String ID = "_id";

    public static final String FOODS_ID = "foodsId";

    public static final String FOODS_NAME_CN = "foodsNameCn";

    public static final String FOODS_NAME_EN = "foodsNameEn";

    public static final String FOODS_IMAGE_CN = "foodsImageCn";

    public static final String FOODS_IMAGE_EN = "foodsImageEn";

    public static final String FOODS_PRICE = "price";

    // 选择的规格id，中间用逗号间隔
    public static final String SPECIALS_ID = "specialsId";

    // 选择的规格name，中间用逗号间隔
    public static final String SPECIALS_NAME_CN = "specialsNameCn";

    // 选择的规格name，中间用逗号间隔
    public static final String SPECIALS_NAME_EN = "specialsNameEn";

    public static final String STORE_NAME_CN = "storeNameCn";

    public static final String STORE_NAME_EN = "storeNameEn";

    public static final String STORE_IMAGE_CN = "storeImageCn";

    public static final String STORE_IMAGE_EN = "storeImageEn";

    public static final String STORE_ID = "storeId";

    private Context mContext;

    public SQLHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cartSql = "create table if not exists " + TABLE_CART +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FOODS_ID + " TEXT , " +
                FOODS_NAME_CN + " TEXT , " +
                FOODS_NAME_EN + " TEXT , " +
                FOODS_IMAGE_CN + " TEXT , " +
                FOODS_IMAGE_EN + " TEXT , " +
                FOODS_PRICE + " TEXT , " +
                SPECIALS_ID + " TEXT , " +
                SPECIALS_NAME_CN + " TEXT , " +
                SPECIALS_NAME_EN + " TEXT , " +
                STORE_NAME_CN + " TEXT , " +
                STORE_NAME_EN + " TEXT , " +
                STORE_IMAGE_CN + " TEXT , " +
                STORE_IMAGE_EN + " TEXT , " +
                STORE_ID + " TEXT)";

        String cardSql = "create table if not exists " + CardConstant.TABLE_CARD +
                "(" + CardConstant.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardConstant.USER_ID + " VARCHAR ," +
                CardConstant.CARD_NO + " TEXT ," +
                CardConstant.CARD_MONTH + " INTEGER ," +
                CardConstant.CARD_YEAR + " INTEGER ," +
                CardConstant.CARD_CVV + " TEXT ," +
                CardConstant.CARD_FIRSTNAME + " TEXT ," +
                CardConstant.CARD_LASTNAME + " TEXT ," +
                CardConstant.CARD_ADDRESS + " TEXT ," +
                CardConstant.CARD_ZIPCODE + " TEXT)";

        db.execSQL(cartSql);
        db.execSQL(cardSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1: {
                if (newVersion <= 1) {
                    return;
                }

                db.beginTransaction();
                try {
                    updateToVersion2(db);
                    db.setTransactionSuccessful();
                } catch (Throwable ex) {
                    break;
                } finally {
                    db.endTransaction();
                }
                break;
            }

            default: {
                break;
            }
        }
    }

    private void updateToVersion2(SQLiteDatabase db) {
        String cardSql = "create table if not exists " + CardConstant.TABLE_CARD +
                "(" + CardConstant.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardConstant.USER_ID + " VARCHAR ," +
                CardConstant.CARD_NO + " TEXT ," +
                CardConstant.CARD_MONTH + " INTEGER ," +
                CardConstant.CARD_YEAR + " INTEGER ," +
                CardConstant.CARD_CVV + " TEXT ," +
                CardConstant.CARD_FIRSTNAME + " TEXT ," +
                CardConstant.CARD_LASTNAME + " TEXT ," +
                CardConstant.CARD_ADDRESS + " TEXT ," +
                CardConstant.CARD_ZIPCODE + " TEXT)";

        db.execSQL(cardSql);
    }
}
