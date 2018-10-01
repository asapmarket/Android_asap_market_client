package com.mhysa.waimai.model.wallet;

/**
 * Created by xusheng3 on 2017/12/25.
 */

public class RewardPoint {

    // 什么是饭点中英文说明
    public String info_cn;

    public String info_en;

    // 兑换比例
    public int ratio;

    // 可用饭点
    public double point;

    // 可用的累计金额
    public double money;

    // 每次最少兑换多少饭点
    public int min_exchange;

    // 总的兑换金额
    public double total_money;

    public String instructions_en;

    public String instructions_cn;
}
