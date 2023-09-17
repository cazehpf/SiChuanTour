package com.cazehpf.service;

import com.cazehpf.entity.MailCode;

/**
 * author:hpf
 **/
public interface EmailService {
    /**
     * 根据邮箱名查询验证码
     * @param name
     * @return
     */
    MailCode getByNameAndCode(String name,String code);

    /**
     * 根据邮箱名查询记录
     * @param name
     * @return
     */
    MailCode getByName(String name);

    /**
     * 新增邮箱验证码
     * @param mailCode
     */
    void add(MailCode mailCode);

    /**
     * 修改邮箱验证码
     * @param mailCode
     */
    void update(MailCode mailCode);
}
