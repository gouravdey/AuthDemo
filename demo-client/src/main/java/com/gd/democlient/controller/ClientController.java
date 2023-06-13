package com.gd.democlient.controller;

import com.gd.democlient.dto.ClientTokenDto;
import jdk.jfr.ContentType;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.gd.democlient.utils.SecurityUtils.createTokenConfig;

@RestController
@RequestMapping("/v1/client")
public class ClientController {

    private final WebClient webClient = WebClient.builder().build();

    Map<String, ClientTokenDto> tokenStore = new HashMap<>();

    @GetMapping("/test")
    public String test() throws Exception {

        // Can be fetched dynamically based on CM
        String clientId = "gd-client2";

        // Can be fetched from properties file
        String tokenEndpoint = "http://localhost:8080/realms/master/protocol/openid-connect/token";

        ClientTokenDto tokenResponse;

        if (tokenStore.containsKey(clientId) && tokenStore.get(clientId).getExpirationTime().getTime() > System.currentTimeMillis()) {

            // Using old token
            System.out.println("Using old token");
            tokenResponse = tokenStore.get(clientId);
        } else {
            System.out.println("Fetching new token");

            // Invoke token endpoint of auth server to get access token
            tokenResponse = webClient.post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(createTokenConfig(clientId, tokenEndpoint))
                    .exchangeToMono(clientResponse -> clientResponse.bodyToMono(ClientTokenDto.class))
                    .block();

            tokenResponse.setExpirationTime(new Date(System.currentTimeMillis() + tokenResponse.getExpiresIn() * 1000));

            tokenStore.put(clientId, tokenResponse);
        }

        System.out.println(tokenResponse);

        // Create headers for invoking APIs of Resource Server
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + tokenResponse.getAccessToken());

        // Invoke APIs of Resource Server
        String response = webClient.get()
                .uri("http://localhost:8091/v1/server/test")
                .headers(header -> header.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response + " --> " + "Returning from client";
    }
}
