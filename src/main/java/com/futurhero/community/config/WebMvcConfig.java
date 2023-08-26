package com.futurhero.community.config;

import com.futurhero.community.controller.interceptor.LoginInterceptor;
import com.futurhero.community.controller.interceptor.LoginRequiredInterceptor;
import com.futurhero.community.controller.interceptor.LogoutRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private LogoutRequiredInterceptor logoutRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor);
        registry.addInterceptor(loginRequiredInterceptor);
        registry.addInterceptor(logoutRequiredInterceptor);
    }
}
