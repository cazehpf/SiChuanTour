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
 * @date: 2023年09月14日 16:36
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class InterestFilter {
    String value;
}
