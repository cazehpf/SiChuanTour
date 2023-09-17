package com.cazehpf.mapper;

import com.cazehpf.entity.MailCode;
import com.cazehpf.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * author:hpf
 **/
@Mapper //在运行时,会自动生成该接口的实现类对象(代理对象), 并且将该对象交给IOC容器管理
public interface MailMapper {
    //根据邮箱和邮箱验证码查询记录
    @Select("select * from MailCode where mailName=#{name} and mailCode=#{code}")
    public MailCode selectByNameAndCode(String name,String code);

    //根据邮箱查询记录
    @Select("select * from MailCode where mailName=#{name}")
    public MailCode selectByName(String name);
    //新增邮箱验证码
    @Insert("insert into MailCode(mailName, mailCode)" +
            " values (#{mailName},#{mailCode})")
    public void insert(MailCode mailCode);

    //修改指定邮箱的验证码
    @Update("update MailCode set mailCode=#{mailCode} where mailName=#{mailName}")
    public void updateMailCode(MailCode mailCode);

}
