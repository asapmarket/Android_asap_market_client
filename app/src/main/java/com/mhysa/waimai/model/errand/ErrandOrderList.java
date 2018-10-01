package com.mhysa.waimai.model.errand;

import java.io.Serializable;
import java.util.List;

/**
 * 文件描述
 * Date: 2018/3/15
 *
 * @author xusheng
 */

public class ErrandOrderList implements Serializable {

    public List<ErrandOrder> rows;

    public int total;

    public int total_page;
}
