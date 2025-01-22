package com.currencyexchange.controller;

import com.currencyexchange.dto.request.CurrencyConversionHistorySearchRequest;
import com.currencyexchange.dto.request.CurrencyConversionRequest;
import com.currencyexchange.dto.response.CurrencyConversionHistoryResponse;
import com.currencyexchange.dto.response.CurrencyConversionHistorySearch;
import com.currencyexchange.dto.response.ErrorEntity;
import com.currencyexchange.dto.response.ExchangeRateResponse;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.exception.GatewayException;
import com.currencyexchange.service.CurrencyConversionService;
import com.currencyexchange.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CurrencyExchangeController {

  private final CurrencyConversionService currencyConversionService;
  private final ExchangeRateService exchangeRateService;

  @Autowired
  public CurrencyExchangeController(
    CurrencyConversionService currencyConversionService, ExchangeRateService exchangeRateService) {
    this.currencyConversionService = currencyConversionService;
    this.exchangeRateService = exchangeRateService;
  }

  @Operation(
    summary = "Get exchange rate between two currencies",
    description = "Returns the current exchange rate between two currencies (source currency and target currency).",
    responses = {
      @ApiResponse(responseCode = "200", description = "Successful exchange rate retrieval",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input provided, e.g., unsupported currency code",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error while fetching exchange rate",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class)))
    }
  )
  @GetMapping("/exchange-rate")
  public ResponseEntity<ExchangeRateResponse> getExchangeRate(
    @Parameter(description = "The source currency code, e.g., USD")
    @RequestParam String sourceCurrencyCode,
    @Parameter(description = "The target currency code, e.g., EUR")
    @RequestParam String targetCurrencyCode) throws GatewayException {
    return ResponseEntity.ok(exchangeRateService.getExchangeRate(sourceCurrencyCode, targetCurrencyCode));
  }

  @Operation(
    summary = "Convert currency from one to another",
    description = "Converts a given amount from source currency to target currency and returns the result, including a transaction ID.",
    responses = {
      @ApiResponse(responseCode = "200", description = "Successful currency conversion",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyConversionHistoryResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input, such as missing or incorrect data",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error while performing conversion",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class)))
    }
  )
  @PostMapping("/convert")
  public ResponseEntity<CurrencyConversionHistoryResponse> convertCurrency(@RequestBody CurrencyConversionRequest request) throws
    GatewayException {
    return ResponseEntity.ok(currencyConversionService.currencyConversion(request));
  }

  @Operation(
    summary = "Search for currency conversion history",
    description = "Fetch a paginated list of currency conversion history based on the provided search criteria (e.g., transaction ID, date range, source, target currencies).",
    responses = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved conversion history",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyConversionHistorySearch.class))),
      @ApiResponse(responseCode = "400", description = "Invalid search criteria provided",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class))),
      @ApiResponse(responseCode = "500", description = "Server error while retrieving history",
                   content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorEntity.class)))
    }
  )
  @GetMapping("/conversion-history/search")
  public ResponseEntity<CurrencyConversionHistorySearch> getConversionHistory(
    @Parameter(description = "Source currency code (e.g., 'USD')", example = "USD")
    @RequestParam(value = "sourceCurrencyCode", required = false) String sourceCurrencyCode,

    @Parameter(description = "Target currency code (e.g., 'EUR')", example = "EUR")
    @RequestParam(value = "targetCurrencyCode", required = false) String targetCurrencyCode,

    @Parameter(description = "Transaction ID", example = "fd30a816-2f38-4a7d-8b09-ea5b53ab1ef7")
    @RequestParam(value = "transactionId", required = false) String transactionId,

    @Parameter(description = "From date in ISO-8601 format (e.g., '2025-01-01T00:00:00')", example = "2025-01-01T00:00:00")
    @RequestParam(value = "fromDate", required = false) LocalDateTime fromDate,

    @Parameter(description = "To date in ISO-8601 format (e.g., '2025-01-31T23:59:59')", example = "2025-01-31T23:59:59")
    @RequestParam(value = "toDate", required = false) LocalDateTime toDate,

    @Parameter(description = "Page number for pagination (e.g., 1)", example = "1")
    @RequestParam(value = "page", required = false) Integer page,

    @Parameter(description = "Number of records per page (e.g., 10)", example = "10")
    @RequestParam(value = "records", required = false) Integer records) {

    CurrencyConversionHistorySearchRequest searchRequest = new CurrencyConversionHistorySearchRequest();
    searchRequest.setSourceCurrencyCode(sourceCurrencyCode);
    searchRequest.setTargetCurrencyCode(targetCurrencyCode);
    searchRequest.setTransactionId(transactionId);
    searchRequest.setFromDate(fromDate);
    searchRequest.setToDate(toDate);
    searchRequest.setPage(page);
    searchRequest.setRecords(records);

    return ResponseEntity.ok(currencyConversionService.searchConversionHistory(searchRequest));
  }
}
