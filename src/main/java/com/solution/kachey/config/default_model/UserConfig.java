package com.solution.kachey.config.default_model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "user.default")
@Data
public class UserConfig {

    @Value("${user.default.username}")
    private String username;

    @Value("${user.default.password}")
    private String password;

    @Value("${user.default.emails}")
    private List<String> emails;

    @Value("${user.default.phoneNumbers}")
    private List<String> phoneNumbers;
}
