package com.github.orelgenya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("simple")
@RefreshScope
public class SimpleController {

    @Value("${my.param}")
    String myParam;

    @Autowired
    Environment env;

    @GetMapping
    public String get() {
        return "Simple micro-service! my-param = " + env.getProperty("my.param") + "|" + myParam;
    }
}
