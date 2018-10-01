package com.mhysa.waimai.model.store;

import com.mhysa.waimai.model.food.Food;

import java.util.List;

/**
 * 食品分类
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class FoodType {

    public int index;

    public String type_id;

    public String type_name_cn;

    public String type_name_en;

    public List<Food> foods_list;
}
