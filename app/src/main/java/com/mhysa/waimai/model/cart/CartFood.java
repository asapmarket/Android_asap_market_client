package com.mhysa.waimai.model.cart;

import com.joey.devilfish.utils.StringUtils;

import java.io.Serializable;

/**
 * 购物车商品
 * Date: 2017/8/3
 *
 * @author xusheng
 */

public class CartFood implements Serializable {

    public String foods_id;

    public String foods_name_cn;

    public String foods_name_en;

    public String foods_image_cn;

    public String foods_image_en;

    public String price;

    // 选择的规格id，中间用逗号间隔
    public String spec_id_list;

    // 选择的规格name，中间用逗号间隔
    public String specials_name_cn;

    // 选择的规格name，中间用逗号间隔
    public String specials_name_en;

    public String store_name_cn;

    public String store_name_en;

    public String store_image_cn;

    public String store_image_en;

    public String store_id;

    public int quality;

    @Override
    public boolean equals(Object o) {

        if (null != o
                && o instanceof CartFood
                && (((CartFood) o).foods_id).equals(foods_id)) {

            if (StringUtils.getInstance().isNullOrEmpty(spec_id_list)
                    && StringUtils.getInstance().isNullOrEmpty(((CartFood) o).spec_id_list)) {
                return true;
            }

            if (!StringUtils.getInstance().isNullOrEmpty(spec_id_list)
                    && !StringUtils.getInstance().isNullOrEmpty(((CartFood) o).spec_id_list)
                    && spec_id_list.equals(((CartFood) o).spec_id_list)) {
                return true;
            }

        }

        return false;
    }
}
