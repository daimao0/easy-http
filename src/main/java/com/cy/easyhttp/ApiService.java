package com.cy.easyhttp;

import com.cy.easyhttp.method.Get;

/**
 * @author Cyan
 * @since v1.0.0
 */
@HttpClient(baseUrl = "https://api.github.com/", headers = {"Accept: application/json", "Content-Type: application/json"})
public interface ApiService {


    @Get(value = "/ping")
    String ping();
}
