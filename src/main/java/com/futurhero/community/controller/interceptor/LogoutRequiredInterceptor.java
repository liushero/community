package com.futurhero.community.controller.interceptor;

import com.futurhero.community.annotation.LogoutRequired;
import com.futurhero.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LogoutRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LogoutRequired annotation = method.getAnnotation(LogoutRequired.class);
            if (annotation != null && hostHolder.getUser() != null) {
                response.sendRedirect(request.getContextPath() + "/index");
                return false;
            }
        }
        return true;
    }
}
