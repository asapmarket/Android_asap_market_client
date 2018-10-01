package com.mhysa.waimai.event;

/**
 * EventID常量
 * <p>
 * Date: 2017/7/25
 *
 * @author xusheng
 */

public class EventID {

    // 修改语言成功
    public static final int CHANGE_LANGUAGE_SUCCESS = 10001;

    // 修改昵称成功
    public static final int CHANGE_NICKNAME_SUCCESS = 10002;

    // 修改头像成功
    public static final int CHANGE_AVATAR_SUCCESS = 10003;

    // 退出登录成功
    public static final int LOGOUT_SUCCESS = 10004;

    // 选择收货地址成功
    public static final int CHOOSE_ADDRESS_SUCCESS = 10005;

    // 新建收货地址成功
    public static final int ADD_ADDRESS_SUCCESS = 10006;

    // 编辑收货地址成功
    public static final int EDIT_ADDRESS_SUCCESS = 10007;

    // 删除收货地址成功
    public static final int DELETE_ADDRESS_SUCCESS = 10008;

    // 取消收藏店家
    public static final int DELETE_STORE_FAVORITE = 10009;

    // 收藏店家
    public static final int ADD_STORE_FAVORITE = 10010;

    // 支付成功
    public static final int PAY_SUCCESS = 10011;

    // 收货地址为空
    public static final int ADDRESS_IS_EMPTY = 10012;

    // 添加信用卡成功
    public static final int ADD_CARD_SUCCESS = 10013;

    // 结算或者提交订单的时候，有商家已经打烊了
    public static final int DO_HAVE_CLOSED_STORE = 10014;
}
