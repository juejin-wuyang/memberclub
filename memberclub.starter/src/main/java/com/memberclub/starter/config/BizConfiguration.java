package com.memberclub.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(2)
@Data
@ConfigurationProperties(prefix = "memberclub.biz")
public class BizConfiguration {

    //自身商户ID
    private String SELF_MERCHANT_ID;


}
