package com.theodore.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/resume/login")){
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            if ("admin".equals(username) && "admin".equals(password)) {
                request.getSession().setAttribute("user", "admin");
                return true;
            }else {
                response.sendRedirect("/");
                return false;
            }
        }
        Object user = request.getSession().getAttribute("user");
        if (user == null){
            response.sendRedirect("/");
            return false;
        }else {
            return true;
        }

    }
}
