package com.cy.easyhttp;

/**
 * @author Cyan
 */
public class Main {
    public static void main(String[] args) {
        ApiService apiService = HttpClientProxyFactory.create(ApiService.class);
        apiService.ping();
        System.out.println(apiService);
    }
}
