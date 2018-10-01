package com.mhysa.waimai.model.food;

import com.mhysa.waimai.model.special.SpecialClass;
import com.mhysa.waimai.model.store.Store;

import java.io.Serializable;
import java.util.List;

/**
 * 食物
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class Food implements Serializable {

    public String foods_id;

    public String foods_name_cn;

    public String foods_name_en;

    public String foods_image_cn;

    public String foods_image_en;

    public String foods_desc_cn;

    public String foods_desc_en;

    // 是否有额外的商品规格
    // 0：没有（直接加入购物车）
    // 1：有（需要调获取商品规格接口，
    // 在弹出页选择规格）
    public String has_spec;

    // 如果没有额外的规则，直接用这个价格。
    // 如果有额外的规格，取相应规格的价格
    public String price;

    public List<SpecialClass> spec_class_list;

    public List<FoodDetail> foods_detail;

    public String state;

    // UI使用
    public int selectedId;

    // UI使用，添加到购物车的该食物数量
    public int count;

    // UI使用，所属类型名称
    public String type_cn;

    public String type_en;

    // 提交订单使用，选择的规格
    public String spec_id_list;

    public int foods_quantity;

    public int quantity;

    public String store_id;

    // 商家
    public Store store;
}
