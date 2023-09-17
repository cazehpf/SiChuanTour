package com.cazehpf.service.impl;/**
 * author:hpf
 **/

import com.cazehpf.entity.MailCode;
import com.cazehpf.mapper.MailMapper;
import com.cazehpf.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月30日 13:42
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailMapper mailMapper;

    @Override
    public MailCode getByNameAndCode(String name, String code) {
        return mailMapper.selectByNameAndCode(name, code);
    }

    @Override
    public MailCode getByName(String name) {
        return mailMapper.selectByName(name);
    }

    @Override
    public void add(MailCode mailCode) {

        mailMapper.insert(mailCode);
    }

    @Override
    public void update(MailCode mailCode) {
        mailMapper.updateMailCode(mailCode);
    }
}
