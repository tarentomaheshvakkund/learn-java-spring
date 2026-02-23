package com.learning.systemdesign.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardingController {

    @RequestMapping(value = "/{path:^(?!api|actuator|h2-console|assets)(?!.*\\.).*$}/**")
    public String forward() {
        return "forward:/index.html";
    }
}
