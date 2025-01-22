package com.currencyexchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExchangeRateAPIService {
  @Value("${fixer.api.key}")
  private String apiKey;
  @Value("${fixer.url}")
  private String apiUrl;
  private final RestTemplate restTemplate;

  @Autowired
  public ExchangeRateAPIService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Map<String, BigDecimal> fetchExchangeRates() {
    String url = String.format(apiUrl, apiKey);

    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

    try {
      Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();

      System.out.println("API Response: " + response);

      if (response != null && response.containsKey("error")) {
        System.err.println("API Error: " + response.get("error"));
        throw new RuntimeException();
      }

      if (response != null && response.containsKey("rates")) {
        Map<String, Object> rates = (Map<String, Object>) response.get("rates");

        return convertRatesToBigDecimal(rates);
      } else {
        System.err.println("Error: 'rates' key is missing or null in the response.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    throw new RuntimeException();
  }

  private Map<String, BigDecimal> convertRatesToBigDecimal(Map<String, Object> rates) {
    Map<String, BigDecimal> bigDecimalRates = new HashMap<>();

    for (Map.Entry<String, Object> entry : rates.entrySet()) {
      if (entry.getValue() instanceof Double) {
        bigDecimalRates.put(entry.getKey(), BigDecimal.valueOf((Double) entry.getValue()));
      } else if (entry.getValue() instanceof BigDecimal) {
        bigDecimalRates.put(entry.getKey(), (BigDecimal) entry.getValue());
      } else {
        System.err.println("Unexpected type for rate: " + entry.getValue().getClass());
      }
    }

    return bigDecimalRates;
  }
}
