package com.comerzzia.tier.librerias.cashlogyclient;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.comerzzia.tier.librerias.cashlogyclient.model.*;

public class CashlogyClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CashlogyClient(String baseUrl) {
        this(createDefaultTemplate(), baseUrl);
    }

    // Inyección opcional de RestTemplate (para tests)
    public CashlogyClient(String baseUrl, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private static RestTemplate createDefaultTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(10_000);
        return new RestTemplate(factory);
    }

    public LoginResponse login(String userName, String password) {
        String url = baseUrl + "/api/Authentication/login";
        LoginRequest req = new LoginRequest(userName, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(req, headers);
        try {
            ResponseEntity<LoginResponse> resp = restTemplate.exchange(
                url, HttpMethod.POST, entity, LoginResponse.class);
            LoginResponse body = resp.getBody();
            if (body != null && body.getToken() != null) {
                body.setResult("OK");
            } else {
                body.setResult("ERROR_GENERIC");
                body.setMensajeError("No se recibió token en la respuesta");
            }
            return body;
        } catch (HttpClientErrorException e) {
            LoginResponse error = new LoginResponse();
            error.setResult("ERROR_GENERIC");
            error.setMensajeError("Error de autenticación: " + e.getStatusCode());
            return error;
        }
    }

    public SaleResponse sale(SaleRequest saleRequest, String bearerToken) throws ResourceAccessException {
        String url = baseUrl + "/api/MiddlewareCommand/Sale";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));

        HttpEntity<SaleRequest> entity = new HttpEntity<>(saleRequest, headers);
        try {
            ResponseEntity<SaleResponse> resp = restTemplate.exchange(
                url, HttpMethod.POST, entity, SaleResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            SaleResponse error = new SaleResponse();
            error.setResult("ERROR_HTTP_" + e.getStatusCode().value());
            error.setMensajeError(e.getResponseBodyAsString());
            return error;
        }
    }

    public CancelSaleResponse cancelSale(CancelSaleRequest cancelSaleRequest, String bearerToken) {
        String url = baseUrl + "/api/MiddlewareCommand/CancelSale";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));

        HttpEntity<CancelSaleRequest> entity = new HttpEntity<>(cancelSaleRequest, headers);
        try {
            ResponseEntity<CancelSaleResponse> resp = restTemplate.exchange(
                url, HttpMethod.POST, entity, CancelSaleResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            CancelSaleResponse error = new CancelSaleResponse();
            error.setResult("ERROR_GENERIC");
            return error;
        }
    }

    public DispenseResponse dispense(DispenseRequest dispenseRequest, String bearerToken) {
        String url = baseUrl + "/api/MiddlewareCommand/Dispense";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));

        HttpEntity<DispenseRequest> entity = new HttpEntity<>(dispenseRequest, headers);
        try {
            ResponseEntity<DispenseResponse> resp = restTemplate.exchange(
                url, HttpMethod.POST, entity, DispenseResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            DispenseResponse error = new DispenseResponse();
            error.setResult("INVALID_AMOUNT");
            error.setMensajeError("Error de dispensación: " + e.getResponseBodyAsString());
            return error;
        }
    }

    public StatusSaleResponse statusSale(String bearerToken) {
        String url = baseUrl + "/api/MiddlewareCommand/StatusSale";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<StatusSaleResponse> resp = restTemplate.exchange(
                url, HttpMethod.GET, entity, StatusSaleResponse.class);
            return resp.getBody();
        } catch (HttpClientErrorException e) {
            StatusSaleResponse error = new StatusSaleResponse();
            error.setResult("ERROR_GENERIC");
            return error;
        }
    }
}
