package com.se2035.jrw.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminRedirectInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() 
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            String uri = request.getRequestURI();
            
            // Redirect admin to dashboard if trying to access non-admin client-side pages
            if (!uri.startsWith("/admin") 
                    && !uri.startsWith("/css") 
                    && !uri.startsWith("/js") 
                    && !uri.startsWith("/images") 
                    && !uri.startsWith("/uploads") 
                    && !uri.equals("/logout") 
                    && !uri.equals("/error") 
                    && !uri.equals("/favicon.ico")) {
                response.sendRedirect("/admin/dashboard");
                return false;
            }
        }
        return true;
    }
}
