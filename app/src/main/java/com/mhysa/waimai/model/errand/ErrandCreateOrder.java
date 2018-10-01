package com.mhysa.waimai.model.errand;

import java.io.Serializable;

/**
 * 文件描述
 * Date: 2018/3/15
 *
 * @author xusheng
 */

public class ErrandCreateOrder implements Serializable {

    public String user_id;

    public String token;

    public ErrandOrder order;
}
