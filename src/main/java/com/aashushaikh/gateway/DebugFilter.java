package com.aashushaikh.gateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Integer.MIN_VALUE)
public class DebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("[REQUEST IN ] " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("[REQUEST IN ] Host header : " + request.getHeader("Host"));
        System.out.println("[REQUEST IN ] Content-Type: " + request.getContentType());

        chain.doFilter(request, response);

        System.out.println("[RESPONSE   ] Status: " + response.getStatus() + " for " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
