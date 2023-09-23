package com.example.demo.model.security;

import com.auth0.jwt.JWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConsts.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConsts.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConsts.HEADER_STRING);

        if (token != null) {
            String userInfo = JWT.require(HMAC512(SecurityConsts.SECRET.getBytes()))
                .build()
                .verify(token.replace(SecurityConsts.TOKEN_PREFIX, ""))
                .getSubject();

            if (userInfo != null)
                return new UsernamePasswordAuthenticationToken(userInfo, null, new ArrayList<>());

            return null;
        }

        return null;
    }

}