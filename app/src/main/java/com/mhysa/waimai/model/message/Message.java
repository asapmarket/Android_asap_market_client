package com.mhysa.waimai.model.message;

import java.io.Serializable;

/**
 * 会员消息
 * Date: 2017/7/30
 *
 * @author xusheng
 */

public class Message implements Serializable {

    public String message_id;

    public String create_time;

    public String title_cn;

    public String title_en;

    public String desc_cn;

    public String desc_en;

    public String image;
}
