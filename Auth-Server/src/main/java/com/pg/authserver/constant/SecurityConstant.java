package com.pg.authserver.constant;

public class SecurityConstant {
    public static final String[] PUBLIC_URLS = {"/login"};
    public static final String ISSUER = "UP";

    public static final long EXPIRATION_TIME = 432_000_000; // 5 DAYS - 5 * 24 * 60 * 60 * 1000

    public static final String AUTHORITIES = "authorities";

    public static final String TOKEN_HEADER = "auth-token";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token can not be verified!";
}
