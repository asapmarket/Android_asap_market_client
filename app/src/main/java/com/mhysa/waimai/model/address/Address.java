package com.mhysa.waimai.model.address;

import java.io.Serializable;

/**
 * 收货地址
 * Date: 2017/7/24
 *
 * @author xusheng
 */

public class Address implements Serializable {

    public String extm_id;

    public String extm_name;

    // 0 女士  1  男士
    public String sex;

    public String extm_phone;

    public String address;

    // 是否是默认收货地址 0：不是 1：是
    public String is_default;

    public String zip_code;

    public String user_id;
}
