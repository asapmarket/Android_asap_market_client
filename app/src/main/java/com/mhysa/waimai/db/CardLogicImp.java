package com.mhysa.waimai.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.WaimaiApplication;
import com.mhysa.waimai.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * 信用卡管理
 * Date: 2018/5/25
 *
 * @author xusheng3
 */

public class CardLogicImp {

    /**
     * 获取所有的信用卡
     *
     * @return
     */
    public static List<Card> getCardList(String userId) {
        List<Card> cards = new ArrayList<Card>();

        if (StringUtils.getInstance().isNullOrEmpty(userId)) {
            return cards;
        }

        SQLiteDatabase database = null;
        Cursor cursor = null;
        String selection = CardConstant.USER_ID + "=? ";
        String[] selectionArgs = new String[]{userId};

        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getReadableDatabase();
            cursor = database.query(false, CardConstant.TABLE_CARD, null, selection, selectionArgs,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                Card card = new Card();
                card.id = cursor.getString(cursor.getColumnIndex(CardConstant.ID));
                card.user_id = cursor.getString(cursor.getColumnIndex(CardConstant.USER_ID));
                card.card_no = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_NO));
                card.month = cursor.getInt(cursor.getColumnIndex(CardConstant.CARD_MONTH));
                card.year = cursor.getInt(cursor.getColumnIndex(CardConstant.CARD_YEAR));
                card.cvv = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_CVV));
                card.address = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_ADDRESS));
                card.firstName = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_FIRSTNAME));
                card.lastName = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_LASTNAME));
                card.zipCode = cursor.getString(cursor.getColumnIndex(CardConstant.CARD_ZIPCODE));

                cards.add(card);
            }
        } catch (Exception exception) {

        } finally {
            if (database != null) {
                database.close();
            }

            if (null != cursor) {
                cursor.close();
            }
        }

        return cards;
    }

    /**
     * 删除卡片
     *
     * @param id
     * @param userId
     */
    public static void deleteCard(String id, String userId) {
        if (StringUtils.getInstance().isNullOrEmpty(id)
                || StringUtils.getInstance().isNullOrEmpty(userId)) {
            return;
        }

        SQLiteDatabase database = null;
        String selection = CardConstant.ID + "=? AND " +
                CardConstant.USER_ID + "=? ";
        String[] selectionArgs = new String[]{id, userId};

        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();
            database.delete(CardConstant.TABLE_CARD, selection, selectionArgs);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 新建卡片
     *
     * @param card
     */
    public static void addCard(Card card) {
        SQLiteDatabase database = null;
        try {
            database = WaimaiApplication.getInstance().getSQLHelper().getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CardConstant.USER_ID, card.user_id);
            values.put(CardConstant.CARD_NO, card.card_no);
            values.put(CardConstant.CARD_MONTH, card.month);
            values.put(CardConstant.CARD_YEAR, card.year);
            values.put(CardConstant.CARD_CVV, card.cvv);
            values.put(CardConstant.CARD_ADDRESS, card.address);
            values.put(CardConstant.CARD_FIRSTNAME, card.firstName);
            values.put(CardConstant.CARD_LASTNAME, card.lastName);
            values.put(CardConstant.CARD_ZIPCODE, card.zipCode);

            database.insert(CardConstant.TABLE_CARD, null, values);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }
}
