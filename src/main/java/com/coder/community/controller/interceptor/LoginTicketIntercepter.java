package com.coder.community.controller.interceptor;

import com.coder.community.entity.LoginTicket;
import com.coder.community.entity.User;
import com.coder.community.service.UserService;
import com.coder.community.util.CookieUtil;
import com.coder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketIntercepter implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1 从Cookie中获取凭证ticket
        String ticket = CookieUtil.getValue(request, "ticket");
        // 2 判断凭证ticket
        if (ticket != null) {
            // 2.1 ticket 不等于null，查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 2.2 判断ticket是否有效，status = 0 and expired.after new Date()
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 2.3 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 2.4 在本次请求中持有用户， 存到ThreadLocal，多线程隔离
                hostHolder.setUsers(user);
                // 构建用户认证的结果，并存入SecurityContext，以便于Security进行授权。
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()) );

                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 在模板引擎之前 把user存到ModelAndView
        // 判断user 和 ModelAndView
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        // 清理
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
