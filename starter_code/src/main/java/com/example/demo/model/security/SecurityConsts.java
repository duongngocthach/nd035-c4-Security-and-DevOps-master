package com.example.demo.model.security;

public class SecurityConsts {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 432_000_000;         // 5 days
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";
}
