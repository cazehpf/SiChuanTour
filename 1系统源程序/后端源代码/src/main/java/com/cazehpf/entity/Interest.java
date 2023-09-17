package com.cazehpf.entity;/**
 * author:hpf
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月06日 15:25
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class Interest {

    private Integer interestID;//景点id
    private String interestName;//景点名
    private String interestDescription;//景点描述
    private String interestLevel;//景点等级
    private String interestCity;//景点所属城市
    private String interestCounty;//景点所属区县
    private String interestAddress;//景点详细地址
    private String interestImg;//景点图片
    private  String  interestAdcode;//景点行政编码

}
