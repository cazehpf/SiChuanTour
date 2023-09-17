package com.cazehpf.entity;/**
 * author:hpf
 **/

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月06日 15:22
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 景区分页查询结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestsPageBean {

    private Long total;//总记录数
    private List rows;//数据列表

}

