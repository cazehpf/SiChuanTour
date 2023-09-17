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
 * @date: 2023年08月30日 13:26
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class MailCode {
    private Integer mailID;//邮箱id
    private String mailName;//邮箱名
    private String mailCode;//邮箱验证码
}
