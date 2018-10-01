package com.mhysa.waimai.model.order;

import com.mhysa.waimai.model.address.Address;
import com.mhysa.waimai.model.store.Store;

import java.util.List;

/**
 * 订单
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class Order {

    public String order_id;

    public List<Store> store_list;

    public String state;

    // 是否催单
    public String is_urge;

    // 配送员id
    public String exp_id;

    // 收货地址
    public Address extm;

    public String extm_id;

    public String payment_method;

    public String remark;

    public String pay_state;

    public String exp_name;

    // 总金额
    public String total_money;

    public String money;

    // 税
    public String tax;

    // 运费
    public String yunfei;

    public String create_time;

    // 配送时间
    public String distribution_time;

}
