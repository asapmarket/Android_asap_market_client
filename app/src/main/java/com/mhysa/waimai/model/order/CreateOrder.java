package com.mhysa.waimai.model.order;

import java.io.Serializable;

/**
 * 文件描述
 * Date: 2017/8/14
 *
 * @author xusheng
 */

public class CreateOrder implements Serializable {

    public String user_id;

    public String token;

    public Order order;

    public String COUPON_NUM;

    public String COUPON_MONEY;

    public String[] store_ids;

    public String store_name_cn;

    public String store_name_en;
}
