package com.dimitrijevic175.warehouse_service.security;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generate(Claims claims);

    Claims parseToken(String jwt);
}
