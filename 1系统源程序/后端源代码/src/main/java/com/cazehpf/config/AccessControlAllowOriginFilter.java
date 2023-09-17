package com.cazehpf.config;/**
 * author:hpf
 **/

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月29日 14:28
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AccessControlAllowOriginFilter implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/*/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(1800)
                .allowedOrigins("*");
    }
}

