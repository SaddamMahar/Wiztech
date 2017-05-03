package com.primeid.config;

import com.primeid.model.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "created";

    private static String secret = "mySecret";

    private static Long expiration = 2L; // days

    private static Date generateExpirationDate() {
        LocalDate localDate = LocalDate.now().plusDays(expiration);
        Date localDatee = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return localDatee;
    }

    public static String generateToken(Account accountDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, accountDetails.getAccountCode());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    static String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS512, secret).compact();
    }
}
