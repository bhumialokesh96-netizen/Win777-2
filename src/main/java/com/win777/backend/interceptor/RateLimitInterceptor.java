package com.win777.backend.interceptor;

import com.win777.backend.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for rate limiting.
 * Applies rate limits to specific endpoints.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestPath = request.getRequestURI();
        
        // Apply rate limiting to sensitive endpoints
        if (shouldRateLimit(requestPath)) {
            String key = getClientKey(request);
            
            if (!rateLimitService.tryConsume(key)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"message\":\"Too many requests. Please try again later.\"}");
                response.setContentType("application/json");
                return false;
            }
        }
        
        return true;
    }

    /**
     * Determines if the request path should be rate limited.
     * 
     * @param path the request path
     * @return true if path should be rate limited
     */
    private boolean shouldRateLimit(String path) {
        return path.equals("/auth/login") || path.equals("/api/sms-tasks/claim");
    }

    /**
     * Gets a unique key for the client (IP address).
     * In production, this could also use userId for authenticated requests.
     * 
     * @param request the HTTP request
     * @return the client key
     */
    private String getClientKey(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress + ":" + request.getRequestURI();
    }
}
