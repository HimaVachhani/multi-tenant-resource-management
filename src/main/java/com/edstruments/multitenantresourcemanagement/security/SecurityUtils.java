package com.edstruments.multitenantresourcemanagement.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Small helper that extracts JWT from Authorization header and returns claims.
 * Uses JwtTokenProvider already provided.
 */
public class SecurityUtils {

    public static String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public static Claims getClaimsFromRequest(HttpServletRequest request, JwtTokenProvider jwtTokenProvider) {
        String token = resolveToken(request);
        if (token == null) return null;
        return jwtTokenProvider.validateAndGetClaims(token);
    }
}
