package com.mhysa.waimai.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.joey.devilfish.utils.MD5;
import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.BuildConfig;
import com.mhysa.waimai.model.address.AddressList;
import com.mhysa.waimai.model.address.Range;
import com.mhysa.waimai.model.banner.Banners;
import com.mhysa.waimai.model.card.CardList;
import com.mhysa.waimai.model.coupon.Coupon;
import com.mhysa.waimai.model.errand.ErrandCreateOrder;
import com.mhysa.waimai.model.errand.ErrandOrder;
import com.mhysa.waimai.model.errand.ErrandOrderList;
import com.mhysa.waimai.model.errand.ErrandRewardPoint;
import com.mhysa.waimai.model.favorite.FavoriteState;
import com.mhysa.waimai.model.food.Food;
import com.mhysa.waimai.model.home.HomeInfo;
import com.mhysa.waimai.model.message.MessageList;
import com.mhysa.waimai.model.order.AmountDes;
import com.mhysa.waimai.model.order.CreateOrder;
import com.mhysa.waimai.model.order.DistributionTimeList;
import com.mhysa.waimai.model.order.Order;
import com.mhysa.waimai.model.order.OrderList;
import com.mhysa.waimai.model.order.PayUrl;
import com.mhysa.waimai.model.order.PaypalNotify;
import com.mhysa.waimai.model.special.Price;
import com.mhysa.waimai.model.store.Store;
import com.mhysa.waimai.model.store.StoreFoodList;
import com.mhysa.waimai.model.store.StoreList;
import com.mhysa.waimai.model.upload.UploadInfo;
import com.mhysa.waimai.model.user.ExpLngLat;
import com.mhysa.waimai.model.user.User;
import com.mhysa.waimai.model.verifycode.VerifyCode;
import com.mhysa.waimai.model.wallet.RewardPoint;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 文件描述
 * Date: 2017/7/27
 *
 * @author xusheng
 */

public class OkHttpClientManager {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClientManager mInstance;

    private OkHttpClientManager() {
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 上传文件
     *
     * @param name
     * @param file
     * @param netWorkListener
     */
    public void uploadInfo(String name, File file, final WtNetWorkListener<UploadInfo> netWorkListener) {
        String url = BuildConfig.BASE_URL + "app/common/upload.do";
        HashMap<String, String> params = new HashMap<>();

        OkHttpUtils.post().params(params)
                .addFile("file", name, file)
                .url(url).build()
                .readTimeOut(5000)
                .writeTimeOut(5000)
                .connTimeOut(3000)
                .execute(new OkhttpCallback(new TypeToken<RemoteReturnData<UploadInfo>>() {
                }.getType()) {
                    @Override
                    public void onResponse(RemoteReturnData response, int id) {
                        super.onResponse(response, id);
                        if (null == netWorkListener) {
                            return;
                        }

                        if (null == response
                                || StringUtils.getInstance().isNullOrEmpty(response.status)) {
                            netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                            return;
                        }

                        String code = response.status;
                        if (code.equals(HttpConstant.MsgCode.MSG_CODE_SUCCESS)) {
                            // 成功
                            netWorkListener.onSucess(response);
                        } else {
                            netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        if (null == netWorkListener) {
                            return;
                        }
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (null == netWorkListener) {
                            return;
                        }
                        netWorkListener.onFinished();
                    }
                });
    }

    private void post(String url, RequestBody requestBody, final Type type, final WtNetWorkListener netWorkListener) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            if (BuildConfig.DEBUG) {
                Log.e("xusheng", "url=" + url);
            }
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
                    if (null != netWorkListener) {
                        netWorkListener.onFinished();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (null != netWorkListener) {
                        netWorkListener.onFinished();
                    }

                    String result = response.body().string();
                    if (BuildConfig.DEBUG) {
                        Log.e("xusheng", "result=" + result);
                    }
                    if (StringUtils.getInstance().isNullOrEmpty(result)) {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, "没有收到数据", "No Data");
                    }
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .create();
                    RemoteReturnData returnData = gson.fromJson(result, type);
                    if (null == returnData) {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, "没有收到数据", "No Data");
                    } else {
                        String code = returnData.status;
                        if (code.equals(HttpConstant.MsgCode.MSG_CODE_SUCCESS)) {
                            // 成功
                            netWorkListener.onSucess(returnData);
                        } else {
                            netWorkListener.onError(code, returnData.msg_cn, returnData.msg_en);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            if (null != netWorkListener) {
                netWorkListener.onFinished();
            }

            if (null != netWorkListener) {
                netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, ex.getMessage(), ex.getMessage());
            }
        }
    }

    private void post(String url, Map<String, String> params, Type type, final WtNetWorkListener netWorkListener) {
        try {
            if (BuildConfig.DEBUG) {
                Log.e("xusheng", "url=" + url);
            }
            OkHttpUtils.post().url(url).params(params).addHeader("Content-Type",
                    "application/json; charset=utf-8").build().execute(new OkhttpCallback(type) {
                @Override
                public void onResponse(RemoteReturnData response, int id) {
                    super.onResponse(response, id);
                    if (null == netWorkListener) {
                        return;
                    }

                    if (null == response || StringUtils.getInstance().isNullOrEmpty(response.status)) {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                        return;
                    }

                    String code = response.status;
                    if (code.equals(HttpConstant.MsgCode.MSG_CODE_SUCCESS)) {
                        // 成功
                        netWorkListener.onSucess(response);
                    } else {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                    }
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    if (null == netWorkListener) {
                        return;
                    }
                    netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
                }

                @Override
                public void onAfter(int id) {
                    super.onAfter(id);
                    if (null == netWorkListener) {
                        return;
                    }
                    netWorkListener.onFinished();
                }
            });
        } catch (Exception e) {
            if (null != netWorkListener) {
                netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
            }
        }
    }

    private void get(String url, Map<String, String> params, Type type, final WtNetWorkListener netWorkListener) {
        try {
            if (null != params && params.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(url);
                Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    stringBuilder.append("&");
                    Map.Entry<String, String> entry = it.next();
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append("=");
                    stringBuilder.append(entry.getValue());
                }

                url = stringBuilder.toString();

                if (url.contains("&")) {
                    url = url.replaceFirst("\\^|&", "?");
                }
            }

            if (BuildConfig.DEBUG) {
                Log.e("xusheng", "url=" + url);
            }
            OkHttpUtils.get().url(url).build().execute(new OkhttpCallback(type) {
                @Override
                public void onResponse(RemoteReturnData response, int id) {
                    super.onResponse(response, id);
                    if (null == netWorkListener) {
                        return;
                    }

                    if (null == response
                            || StringUtils.getInstance().isNullOrEmpty(response.status)) {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                        return;
                    }

                    String code = response.status;
                    if (code.equals(HttpConstant.MsgCode.MSG_CODE_SUCCESS)) {
                        // 成功
                        netWorkListener.onSucess(response);
                    } else {
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                    }
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    if (null == netWorkListener) {
                        return;
                    }
                    netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
                }

                @Override
                public void onAfter(int id) {
                    super.onAfter(id);
                    if (null == netWorkListener) {
                        return;
                    }
                    netWorkListener.onFinished();
                }
            });
        } catch (Exception ex) {
            if (null != netWorkListener) {
                netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, ex.getMessage(), ex.getMessage());
            }
        }
    }

    /**
     * 登录
     *
     * @param phone
     * @param pwd
     * @param wtNetWorkListener
     */
    public void login(String phone, String pwd, WtNetWorkListener<User> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/login.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("pwd", MD5.md5(pwd));
        this.get(url, params, new TypeToken<RemoteReturnData<User>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 注册
     *
     * @param phone
     * @param pwd
     * @param code
     * @param wtNetWorkListener
     */
    public void register(String phone, String pwd, String code, WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/userReg.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("pwd", MD5.md5(pwd));
        params.put("code", code);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 验证码
     *
     * @param phone
     * @param wtNetWorkListener
     */
    public void verifyCode(String phone, WtNetWorkListener<VerifyCode> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/getCerCode.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        this.get(url, params, new TypeToken<RemoteReturnData<VerifyCode>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 修改密码
     *
     * @param old_pwd
     * @param new_pwd
     * @param token
     * @param wtNetWorkListener
     */
    public void changePwd(String old_pwd, String new_pwd,
                          String token, String user_id,
                          WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/updatePwd.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("old_pwd", MD5.md5(old_pwd));
        params.put("new_pwd", MD5.md5(new_pwd));
        params.put("token", token);
        params.put("user_id", user_id);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 修改昵称
     *
     * @param nick_name
     * @param token
     * @param user_id
     * @param wtNetWorkListener
     */
    public void changeNickname(String nick_name,
                               String token, String user_id,
                               WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/updateNickName.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("nick_name", nick_name);
        params.put("token", token);
        params.put("user_id", user_id);
        this.post(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 修改头像
     *
     * @param head_image
     * @param token
     * @param user_id
     * @param wtNetWorkListener
     */
    public void changeAvatar(String head_image,
                             String token, String user_id,
                             WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/updateHead.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("head_image", head_image);
        params.put("token", token);
        params.put("user_id", user_id);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 找回密码
     *
     * @param phone
     * @param code
     * @param new_pwd
     * @param wtNetWorkListener
     */
    public void findPassword(String phone,
                             String code, String new_pwd,
                             WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/login/forgetPwd.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("new_pwd", MD5.md5(new_pwd));
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取会员消息列表
     *
     * @param user_id
     * @param token
     * @param page
     * @param page_size
     * @param wtNetWorkListener
     */
    public void getMessages(String user_id, String token,
                            int page, int page_size,
                            WtNetWorkListener<MessageList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/message/getMessageList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("page", page + "");
        params.put("page_size", page_size + "");
        this.get(url, params, new TypeToken<RemoteReturnData<MessageList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 删除会员消息
     *
     * @param user_id
     * @param token
     * @param message_id
     * @param wtNetWorkListener
     */
    public void deleteMessage(String user_id, String token,
                              String message_id,
                              WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/message/delMessage.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("message_id", message_id);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取首页数据
     *
     * @param wtNetWorkListener
     */
    public void getHomeInfo(WtNetWorkListener<HomeInfo> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getHomePage.do";
        HashMap<String, String> params = new HashMap<>();
        this.get(url, params, new TypeToken<RemoteReturnData<HomeInfo>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取商家列表
     *
     * @param page
     * @param page_size
     * @param type              type==0时候，代表获取首页商家列表
     * @param wtNetWorkListener
     */
    public void getStoreList(int page, int page_size, String type, String type_id,
                             WtNetWorkListener<StoreList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getStoreListNew.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("page_size", page_size + "");
        if (!StringUtils.getInstance().isNullOrEmpty(type)) {
            params.put("type", type);
        }

        if (!StringUtils.getInstance().isNullOrEmpty(type_id)) {
            params.put("type_id", type_id);
        }

        this.get(url, params, new TypeToken<RemoteReturnData<StoreList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取用户收藏的商家列表
     *
     * @param page
     * @param page_size
     * @param wtNetWorkListener
     */
    public void getFavoriteStoreList(String user_id, String token,
                                     int page, int page_size,
                                     WtNetWorkListener<StoreList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getCollectionStoreList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("page", page + "");
        params.put("page_size", page_size + "");
        this.get(url, params, new TypeToken<RemoteReturnData<StoreList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 订单列表
     *
     * @param user_id
     * @param token
     * @param page
     * @param page_size
     */
    public void getOrderList(String user_id, String token,
                             int page, int page_size, String state,
                             WtNetWorkListener<OrderList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/getOrderList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("page", page + "");
        params.put("page_size", page_size + "");
        params.put("state", state);
        this.get(url, params, new TypeToken<RemoteReturnData<OrderList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取订单详情
     *
     * @param user_id
     * @param token
     * @param order_id
     * @param wtNetWorkListener
     */
    public void getOrderDetail(String user_id, String token,
                               String order_id,
                               WtNetWorkListener<Order> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/getOrderDetail.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("order_id", order_id);
        this.get(url, params, new TypeToken<RemoteReturnData<Order>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 收藏 或者 取消收藏商家
     *
     * @param user_id
     * @param token
     * @param store_id
     * @param wtNetWorkListener
     */
    public void collectOrUnCollect(String user_id,
                                   String token,
                                   String store_id,
                                   WtNetWorkListener<FavoriteState> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/collectStore.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("store_id", store_id);
        this.get(url, params, new TypeToken<RemoteReturnData<FavoriteState>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取商家详情
     *
     * @param user_id
     * @param token
     * @param store_id
     * @param wtNetWorkListener
     */
    public void getStoreDetail(String user_id,
                               String token,
                               String store_id,
                               WtNetWorkListener<Store> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getStoreDetail.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("store_id", store_id);
        this.get(url, params, new TypeToken<RemoteReturnData<Store>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取商家商品列表
     *
     * @param user_id
     * @param token
     * @param store_id
     * @param wtNetWorkListener
     */
    public void getStoreFoodList(String user_id,
                                 String token,
                                 String store_id,
                                 WtNetWorkListener<StoreFoodList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getStoreFoodsList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("store_id", store_id);
        this.get(url, params, new TypeToken<RemoteReturnData<StoreFoodList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 用户选择规格之后，计算价格
     *
     * @param user_id
     * @param token
     * @param foods_id
     * @param spec_id_list      将选择的id用逗号分割，id按升序排序
     * @param wtNetWorkListener
     */
    public void getSpecialPrice(String user_id,
                                String token,
                                String foods_id,
                                String spec_id_list,
                                WtNetWorkListener<Price> wtNetWorkListener) {
        if (StringUtils.getInstance().isNullOrEmpty(spec_id_list)) {
            return;
        }

        String[] specialIds = spec_id_list.split(",");
        if (null == specialIds
                || specialIds.length == 0) {
            return;
        }

        final int length = specialIds.length;
        int a[] = new int[length];
        for (int i = 0; i < length; i++) {
            a[i] = Integer.valueOf(specialIds[i]);
        }

        int temp = 0;
        for (int i = length - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                if (a[j + 1] < a[j]) {
                    temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(a[i]);

            if (i != length - 1) {
                stringBuilder.append(",");
            }
        }

        String url = BuildConfig.BASE_URL + "app/store/getFoodsPrice.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("foods_id", foods_id);
        params.put("spec_id_list", stringBuilder.toString());
        this.get(url, params, new TypeToken<RemoteReturnData<Price>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取商品详情
     *
     * @param user_id
     * @param token
     * @param store_id
     * @param foods_id
     * @param wtNetWorkListener
     */
    public void getFoodDetail(String user_id,
                              String token,
                              String store_id,
                              String foods_id,
                              WtNetWorkListener<Food> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getFoodsDetail.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("store_id", store_id);
        params.put("foods_id", foods_id);
        this.get(url, params, new TypeToken<RemoteReturnData<Food>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 生成订单
     *
     * @param createOrder
     * @param wtNetWorkListener
     */
    public void createOrder(CreateOrder createOrder,
                            WtNetWorkListener<CreateOrder> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/createOrder.do";
        Gson gson = new Gson();
        String json = gson.toJson(createOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<CreateOrder>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 支付
     *
     * @param orderId
     * @param wtNetWorkListener
     */
    public void pay(String orderId,
                    WtNetWorkListener<PayUrl> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/pay.do";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", orderId);
        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<PayUrl>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 根据邮编判断地址是否在配送范围内
     *
     * @param zip_code
     * @param wtNetWorkListener
     */
    public void inRange(String zip_code,
                        WtNetWorkListener<Range> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/address/isEffZipCode.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("zip_code", zip_code);
        this.get(url, params, new TypeToken<RemoteReturnData<Range>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取收货地址列表
     *
     * @param user_id
     * @param token
     * @param wtNetWorkListener
     */
    public void getAddressList(String user_id,
                               String token,
                               WtNetWorkListener<AddressList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/address/getAddressList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        this.get(url, params, new TypeToken<RemoteReturnData<AddressList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 新增收货地址
     *
     * @param user_id
     * @param token
     * @param extm_phone
     * @param extm_name
     * @param sex
     * @param address
     * @param wtNetWorkListener
     */
    public void addAddress(String user_id,
                           String token,
                           String extm_phone,
                           String extm_name,
                           String sex,
                           String address,
                           String zipCode,
                           WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/address/addAddress.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("extm_phone", extm_phone);
        params.put("extm_name", extm_name);
        params.put("sex", sex);
        params.put("address", address);
        params.put("zip_code", zipCode);
        this.post(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 修改收货地址
     *
     * @param user_id
     * @param token
     * @param extm_phone
     * @param extm_name
     * @param sex
     * @param address
     * @param extm_id
     * @param is_default
     * @param wtNetWorkListener
     */
    public void updateAddress(String user_id,
                              String token,
                              String extm_phone,
                              String extm_name,
                              String sex,
                              String address,
                              String extm_id,
                              String is_default,
                              String zipCode,
                              WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/address/editAddress.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("extm_phone", extm_phone);
        params.put("extm_name", extm_name);
        params.put("sex", sex);
        params.put("address", address);
        params.put("extm_id", extm_id);
        params.put("zip_code", zipCode);
        params.put("is_default", is_default);
        this.post(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 删除收货地址
     *
     * @param user_id
     * @param token
     * @param extm_id
     * @param wtNetWorkListener
     */
    public void deleteAddress(String user_id, String token,
                              String extm_id,
                              WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/address/delAddress.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("extm_id", extm_id);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取配送员的位置信息
     *
     * @param user_id
     * @param token
     * @param exp_id
     * @param wtNetWorkListener
     */
    public void getExpLocation(String user_id, String token,
                               String exp_id, WtNetWorkListener<ExpLngLat> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/getExpLngLat.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("exp_id", exp_id);
        this.get(url, params, new TypeToken<RemoteReturnData<ExpLngLat>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取banner
     *
     * @param type              banner类型
     * @param wtNetWorkListener
     */
    public void getBanners(String type, WtNetWorkListener<Banners> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/store/getBanner.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("type", type);
        this.post(url, params, new TypeToken<RemoteReturnData<Banners>>() {
        }.getType(), wtNetWorkListener);
    }

    public void paypalNotify(PaypalNotify paypalNotify,
                             WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/paypal_notify.do";
        Gson gson = new Gson();
        String json = gson.toJson(paypalNotify);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 信用卡支付
     *
     * @param order_id
     * @param stripe_token
     * @param order_type        0 外卖 1跑腿
     * @param wtNetWorkListener
     */
    public void creditCardPay(String order_id, String stripe_token,
                              String order_type, WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/authorize_pay.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("order_id", order_id);
        params.put("stripe_token", stripe_token);
        params.put("order_type", order_type);
        this.post(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 订单金额说明
     *
     * @param createOrder
     * @param wtNetWorkListener
     */
    public void countAmount(CreateOrder createOrder,
                            WtNetWorkListener<AmountDes> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/countOrderAmount.do";
        Gson gson = new Gson();
        String json = gson.toJson(createOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<AmountDes>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 优惠券验证
     *
     * @param coupon
     * @param wtNetWorkListener
     */
    public void userCoupon(Coupon coupon,
                           WtNetWorkListener<Coupon> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/useCoupon.do";
        Gson gson = new Gson();
        String json = gson.toJson(coupon);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<Coupon>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取信用卡列表
     *
     * @param user_id
     * @param token
     * @param wtNetWorkListener
     */
    public void getCardList(String user_id, String token,
                            WtNetWorkListener<CardList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/member/getCardList.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        this.get(url, params, new TypeToken<RemoteReturnData<CardList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 保存信用卡
     *
     * @param user_id
     * @param token
     * @param card_no
     * @param expiration_date
     * @param cvv
     * @param address
     * @param first_name
     * @param last_name
     * @param zip_code
     * @param wtNetWorkListener
     */
    public void saveCard(String user_id, String token,
                         String card_no,
                         String expiration_date,
                         String cvv, String address, String first_name, String last_name,
                         String zip_code,
                         WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/member/saveCard.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("card_no", card_no);
        params.put("expiration_date", MD5.md5(expiration_date));
        params.put("cvv", cvv);
        params.put("address", address);
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("zip_code", zip_code);
        this.post(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 删除信用卡
     *
     * @param user_id
     * @param token
     * @param id
     * @param wtNetWorkListener
     */
    public void deleteCard(String user_id, String token, String id,
                           WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/member/delCard.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("id", id);
        this.post(url, params, new TypeToken<RemoteReturnData<Banners>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取饭点详情
     *
     * @param user_id
     * @param token
     * @param wtNetWorkListener
     */
    public void getRewardPoint(String user_id,
                               String token,
                               WtNetWorkListener<RewardPoint> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/wallet/getWalletPage.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        this.get(url, params, new TypeToken<RemoteReturnData<RewardPoint>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 兑换饭点
     *
     * @param user_id
     * @param token
     * @param wtNetWorkListener
     */
    public void exchange(String user_id,
                         String token,
                         WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/wallet/exchange.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        this.get(url, params, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取配送时间列表
     *
     * @param zipCode
     */
    public void getDistributionTimeListByZipCode(String zipCode,
                                                 WtNetWorkListener<DistributionTimeList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/order/getDistributionTimeListByZipCode.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("zip_code", zipCode);
        this.get(url, params, new TypeToken<RemoteReturnData<DistributionTimeList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取跑腿订单消耗的饭点
     *
     * @param token
     * @param user_id
     * @param wtNetWorkListener
     */
    public void getErrandRewardPoint(String token, String user_id,
                                     WtNetWorkListener<ErrandRewardPoint> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/getUserPoint.do";
        User user = new User();
        user.token = token;
        user.user_id = user_id;
        Gson gson = new Gson();
        String json = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandRewardPoint>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 生成订单
     *
     * @param createOrder
     * @param wtNetWorkListener
     */
    public void errandCreateOrder(ErrandCreateOrder createOrder,
                                  WtNetWorkListener<ErrandOrder> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/createOrder.do";
        Gson gson = new Gson();
        String json = gson.toJson(createOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandOrder>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 上传文件
     *
     * @param name
     * @param table_name
     * @param foreign_key_id
     * @param file
     * @param netWorkListener
     */
    public void uploadInfo(String name, String table_name, String foreign_key_id, File file, final WtNetWorkListener<UploadInfo> netWorkListener) {
        String url = BuildConfig.BASE_URL + "app/common/uploadNew.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("table_name", table_name);
        params.put("foreign_key_id", foreign_key_id);

        OkHttpUtils.post().params(params)
                .addFile("file", name, file)
                .url(url).build()
                .readTimeOut(5000)
                .writeTimeOut(5000)
                .connTimeOut(3000)
                .execute(new OkhttpCallback(new TypeToken<RemoteReturnData<UploadInfo>>() {
                }.getType()) {
                    @Override
                    public void onResponse(RemoteReturnData response, int id) {
                        super.onResponse(response, id);
                        if (null == netWorkListener) {
                            return;
                        }

                        if (null == response
                                || StringUtils.getInstance().isNullOrEmpty(response.status)) {
                            netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                            return;
                        }

                        String code = response.status;
                        if (code.equals(HttpConstant.MsgCode.MSG_CODE_SUCCESS)) {
                            // 成功
                            netWorkListener.onSucess(response);
                        } else {
                            netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, response.msg_cn, response.msg_en);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        if (null == netWorkListener) {
                            return;
                        }
                        netWorkListener.onError(HttpConstant.MsgCode.MSG_CODE_FAIL, e.getMessage(), e.getMessage());
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (null == netWorkListener) {
                            return;
                        }
                        netWorkListener.onFinished();
                    }
                });
    }

    /**
     * 获取订单列表
     *
     * @param token
     * @param user_id
     * @param wtNetWorkListener
     */
    public void getErrandOrderList(String token, String user_id,
                                   int page, int page_size, String state,
                                   WtNetWorkListener<ErrandOrderList> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/getOrderList.do";

        ErrandOrder errandOrder = new ErrandOrder();
        errandOrder.user_id = user_id;
        errandOrder.token = token;
        errandOrder.page = page + "";
        errandOrder.page_size = page_size + "";
        errandOrder.state = state;
        Gson gson = new Gson();
        String json = gson.toJson(errandOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandOrderList>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 跑腿订单详情
     *
     * @param user_id
     * @param token
     * @param order_id
     * @param wtNetWorkListener
     */
    public void getErrandOrderDetail(String user_id, String token,
                                     String order_id,
                                     WtNetWorkListener<ErrandOrder> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/getOrderDetail.do";
        ErrandOrder errandOrder = new ErrandOrder();
        errandOrder.user_id = user_id;
        errandOrder.token = token;
        errandOrder.order_id = order_id;
        Gson gson = new Gson();
        String json = gson.toJson(errandOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandOrder>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 取消跑腿订单
     *
     * @param user_id
     * @param token
     * @param order_id
     * @param state
     * @param wtNetWorkListener
     */
    public void cancelErrandOrder(String user_id, String token,
                                  String order_id, String state,
                                  WtNetWorkListener<ErrandOrder> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/updateOrderErrandsState.do";
        ErrandOrder errandOrder = new ErrandOrder();
        errandOrder.user_id = user_id;
        errandOrder.token = token;
        errandOrder.order_id = order_id;
        errandOrder.state = state;
        Gson gson = new Gson();
        String json = gson.toJson(errandOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandOrder>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 修改支付方式
     *
     * @param user_id
     * @param token
     * @param order_id
     * @param payment_method
     * @param wtNetWorkListener
     */
    public void setErrandPayment(String user_id, String token,
                                 String order_id, String payment_method,
                                 WtNetWorkListener<ErrandOrder> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/updateOrderErrandsPaymentMethod.do";
        ErrandOrder errandOrder = new ErrandOrder();
        errandOrder.user_id = user_id;
        errandOrder.token = token;
        errandOrder.order_id = order_id;
        errandOrder.payment_method = payment_method;
        Gson gson = new Gson();
        String json = gson.toJson(errandOrder);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<ErrandOrder>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * paypal支付回调
     *
     * @param paypalNotify
     * @param wtNetWorkListener
     */
    public void paypalNotifyErrand(PaypalNotify paypalNotify,
                                   WtNetWorkListener<JsonElement> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/orderErrands/paypalNotifyAndroid.do";
        Gson gson = new Gson();
        String json = gson.toJson(paypalNotify);
        RequestBody requestBody = RequestBody.create(JSON, json);
        this.post(url, requestBody, new TypeToken<RemoteReturnData<JsonElement>>() {
        }.getType(), wtNetWorkListener);
    }

    /**
     * 获取饭点详情
     *
     * @param user_id
     * @param token
     * @param wtNetWorkListener
     */
    public void getHowToGetRewardPoint(String user_id,
                                       String token,
                                       WtNetWorkListener<RewardPoint> wtNetWorkListener) {
        String url = BuildConfig.BASE_URL + "app/bulletin/howToGetPoint.do";
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("token", token);
        this.post(url, params, new TypeToken<RemoteReturnData<RewardPoint>>() {
        }.getType(), wtNetWorkListener);
    }
}
