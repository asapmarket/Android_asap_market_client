package com.mhysa.waimai.model.store;

import com.mhysa.waimai.model.food.Food;

import java.io.Serializable;
import java.util.List;

/**
 * 商家
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class Store implements Serializable {

    public String store_id;

    public String store_name_cn;

    public String store_name_en;

    public String store_address_cn;

    public String store_address_en;

    public String store_desc_cn;

    public String store_desc_en;

    public String store_image;

    // 营业时间
    public String business_hours_cn;

    public String business_hours_en;

    // 当前登录用户是否已经收藏
    public int has_collect;

    // 商品数量
    // 订单列表
    public int foods_count;

    public List<Food> foods_list;

    // 订单列表使用
    public String quantity;

    public String onwork;

    @Override
    public boolean equals(Object o) {

        if (null != o
                && o instanceof Store
                && (((Store) o).store_id).equals(store_id)) {
            return true;
        }

        return false;
    }
}
