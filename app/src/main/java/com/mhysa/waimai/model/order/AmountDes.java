package com.mhysa.waimai.model.order;

import com.mhysa.waimai.model.address.Address;

import java.io.Serializable;

/**
 * 说明
 * Date: 2017/9/8
 *
 * @author xusheng
 */

public class AmountDes implements Serializable {

    public String money_limit;

    public String count_words_cn;

    public String count_words_en;

    public double point_balance;

    public Address default_address;

    public String[] store_ids;

    public String yunfei;

    public String tax;

    public String total_money;

    public String store_name_cn;

    public String store_name_en;
}
