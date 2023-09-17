package com.cazehpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class TourgisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourgisApplication.class, args);
    }

}
