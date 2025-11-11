package com.hj.crypto.common.bithumb.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hj.crypto.common.bithumb.model.BithumbAccount;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BithumbService {

    @Value("${bithumb.api.base-url}")
    private String baseUrl;

    @Value("${bithumb.api-key}")
    private String accessKey;

    @Value("${bithumb.secret-key}")
    private String secretKey;

    // 계정 계좌 정보 조회
    public List<BithumbAccount> getAccounts() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("timestamp", System.currentTimeMillis())
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            RestClient restClient = RestClient.create();
            String response = restClient.get()
                    .uri(baseUrl + "/v1/accounts")
                    .header("Authorization", authenticationToken)
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();
            List<BithumbAccount> accounts = mapper.readValue(response, new TypeReference<List<BithumbAccount>>() {
            });

            log.debug("Parsed Accounts: {}", accounts);
            return accounts;
        } catch (Exception e) {
            throw new RuntimeException("Bithumb API 호출 중 오류 발생", e);
        }
    }

    // 주문
    public String placeOrder(String market, String side, double volume, double price, String ordType) {
        try {
            // 요청 바디 생성
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("market", market);
            requestBody.put("side", side); // "bid" = 매수, "ask" = 매도
            requestBody.put("volume", volume);
            requestBody.put("price", price);
            requestBody.put("ord_type", ordType); // "limit" or "price" or "market"

            log.info("requestBody : {}", requestBody);

            // 쿼리 파라미터 해시 생성
            String query = requestBody.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(java.util.stream.Collectors.joining("&"));

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(query.getBytes(StandardCharsets.UTF_8));
            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

            // JWT 생성
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                    .withClaim("access_key", accessKey)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("timestamp", System.currentTimeMillis())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            // API 요청
            RestClient restClient = RestClient.create();
            String response = restClient.post()
                    .uri(baseUrl + "/v1/orders")
                    .header("Authorization", authenticationToken)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            log.info("Order Response: {}", response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Bithumb 주문 API 호출 실패", e);
        }
    }
}
