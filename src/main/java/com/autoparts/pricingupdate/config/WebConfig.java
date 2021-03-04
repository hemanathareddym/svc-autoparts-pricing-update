package com.autoparts.pricingupdate.config;

import java.util.List;

import com.autoparts.pricingupdate.base.MsResponseArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
 
    @Override
    public void addArgumentResolvers(
      List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MsResponseArgumentResolver());
    }
}