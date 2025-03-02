package com.myapp.ecommerce.service;

public interface InvalidatedTokenService {

    boolean checkToken(String token);

    void clearToken(String token);

}
