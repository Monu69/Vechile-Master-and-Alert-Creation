package com.elogist.vehicle_master_and_alert_creation.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

public class JWTUtil {
    public static Integer getAdUser(String authKey){
        int i = authKey.lastIndexOf('.');
        String withoutSignature = authKey.substring(0, i+1);
        Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
        Object adUserTemp = untrusted.getBody().get("id");
        Integer adUser = Integer.parseInt(adUserTemp.toString());
        return adUser;
    }
}