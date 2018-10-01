package com.mhysa.waimai.model.card;

import java.io.Serializable;

/**
 * 文件描述
 * Date: 2017/9/19
 *
 * @author xusheng
 */

public class Card implements Serializable {

    public String id;

    public String user_id;

    public String card_no;

    public int month;

    public int year;

    public String cvv;

    public String address;

    public String lastName;

    public String firstName;

    public String zipCode;
}
