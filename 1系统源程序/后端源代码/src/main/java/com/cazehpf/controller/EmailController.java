package com.cazehpf.controller;/**
 * author:hpf
 * <p>
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月29日 21:21
 * <p>
 * 功能描述
 * @author: hpf
 * @date: 2023年08月29日 21:21
 */

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月29日 21:21
 */

import com.cazehpf.entity.MailCode;
import com.cazehpf.entity.Result;
import com.cazehpf.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
//@CrossOrigin
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    JavaMailSender javaMailSender; //可直接注入邮件发送的对象

    @Value("${spring.mail.username}") //动态获取配置文件中 发送邮件的账户
    private String sendemail;

    /**
     * 发送验证码
     * @param email
     * @return
     */
    @GetMapping("/sendEmailCode")
    public Result sendEmailCode(@RequestParam String email) {

        //随机生成一个四位数的验证码
        int yzm = new Random().nextInt(9999) % (9999 - 1000 + 1) + 1000;
        //创建简单邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        //谁发的
        message.setFrom(sendemail);
        //谁要接收
        message.setTo(email);
        //邮件标题
        message.setSubject("邮件验证码");
        //邮件内容
        message.setText("您的验证码为" + yzm + ",请在30分钟内验证，系统邮件请勿回复！");
        javaMailSender.send(message);


        //发送后，将验证码和邮箱存储到MailCode数据库中。1.如果没有则insert。2.如果数据库中存在邮箱，则修改；
        MailCode mailCode = emailService.getByName(email);
        if (mailCode == null) {
            mailCode = new MailCode();
            mailCode.setMailName(email);
            mailCode.setMailCode(yzm + "");
            emailService.add(mailCode);
        } else {
            mailCode.setMailCode(yzm+"");
            emailService.update(mailCode);
        }

        return Result.success();
    }

    /**
     * 根据给定的邮箱和验证码查询邮箱库是否存在
     * @param email
     * @return
     */
    @GetMapping("/checkEmailCode")
    public Result getEmailCode(@RequestParam String email, String code) {
        MailCode mailCode = emailService.getByNameAndCode(email, code);
        if (mailCode == null) {
            return Result.errorParam("验证失败！请检查");
        }
        return Result.success("验证成功！");

    }
}

