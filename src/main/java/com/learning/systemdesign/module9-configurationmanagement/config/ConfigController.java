package com.learning.systemdesign.module9_configurationmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v9/config")
public class ConfigController {

    @Value("${app.environment.name}")
    private String environmentName;

    @Value("${app.feature.beta-enabled}")
    private boolean betaEnabled;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @GetMapping
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("environment", environmentName);
        config.put("betaFeatures", betaEnabled);
        config.put("connectedDatabase", dbUrl);
        return config;
    }
}
