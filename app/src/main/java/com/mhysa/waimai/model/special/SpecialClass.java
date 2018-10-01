package com.mhysa.waimai.model.special;

import java.io.Serializable;
import java.util.List;

/**
 * 规格分类
 * Date: 2017/7/31
 *
 * @author xusheng
 */

public class SpecialClass implements Serializable{

    public String spec_class_id;

    public String spec_class_name_cn;

    public String spec_class_name_en;

    public List<Special> spec_list;

}
