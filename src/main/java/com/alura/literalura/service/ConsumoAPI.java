package com.alura.literalura.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ConsumoAPI {
    private static final Logger logger = LoggerFactory.getLogger(ConsumoAPI.class);
    private final String API_BASE = "https://gutendex.com/books/";

    public String obterDados(String titulo) {
        try {
            String query = titulo.trim();
            String url = UriComponentsBuilder.fromHttpUrl(API_BASE)
                    .queryParam("search", query)
                    .toUriString();

            logger.debug("Consultando Gutendex - URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.USER_AGENT, "literalura-app/1.0 (Java " + System.getProperty("java.version") + ")");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new RestClientException("Resposta não 2xx: " + resp.getStatusCode());
            }

            String body = resp.getBody();
            logger.debug("Resposta Gutendex (até 2000 chars):\n{}", body == null ? "null" : body.substring(0, Math.min(2000, body.length())));
            return body;
        } catch (Exception e) {
            logger.error("Erro ao consultar Gutendex: {}", e.toString(), e);
            throw new RuntimeException("Erro ao consultar Gutendex: " + e.getMessage(), e);
        }
    }
}
