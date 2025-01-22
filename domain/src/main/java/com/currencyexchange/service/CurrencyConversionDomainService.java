package com.currencyexchange.service;

import com.currencyexchange.adapter.DomainAdapter;
import com.currencyexchange.entity.CurrencyConversionHistory;
import com.currencyexchange.exception.DomainException;
import com.currencyexchange.repository.ConversionHistoryRepository;
import com.currencyexchange.repository.specification.ConversionHistorySpecification;
import com.currencyexchange.value.VCurrencyConversionHistory;
import com.currencyexchange.value.VCurrencyConversionHistorySearch;
import com.currencyexchange.value.request.VCurrencyConversionHistoryRequest;
import com.currencyexchange.value.request.VCurrencyConversionHistorySearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CurrencyConversionDomainService {

  private final DomainAdapter domainAdapter;
  private final ConversionHistoryRepository conversionHistoryRepository;
  private final CurrencyDomainService currencyDomainService;

  @Autowired
  public CurrencyConversionDomainService(
    DomainAdapter domainAdapter, ConversionHistoryRepository conversionHistoryRepository,
    CurrencyDomainService currencyDomainService) {
    this.domainAdapter = domainAdapter;
    this.conversionHistoryRepository = conversionHistoryRepository;
    this.currencyDomainService = currencyDomainService;
  }

  public VCurrencyConversionHistory createCurrencyConversionHistory(VCurrencyConversionHistoryRequest request) throws
    DomainException {

    CurrencyConversionHistory entity = CurrencyConversionHistory.builder()
      .sourceCurrency(currencyDomainService.getCurrency(request.getSourceCurrencyId()))
      .targetCurrency(currencyDomainService.getCurrency(request.getTargetCurrencyId()))
      .amount(request.getAmount())
      .convertedAmount(request.getConvertedAmount())
      .timestamp(LocalDateTime.now())
      .transactionId(generateTransactionId())
      .build();

    log.info("Persisting new currency conversion history to database");

    return domainAdapter.
      fromEntityToValue(conversionHistoryRepository.save(entity));
  }

  private static String generateTransactionId() {
    return UUID.randomUUID().toString();
  }

  public VCurrencyConversionHistorySearch searchConversionHistory(VCurrencyConversionHistorySearchRequest vSearchRequest) {

    log.info("Search criteria: {}", vSearchRequest);

    List<CurrencyConversionHistory> entities;
    List<VCurrencyConversionHistory> vCurrencyConversionHistoryList = new ArrayList<>();
    Pageable pageRequest = null;
    Sort sorting = Sort.by(Sort.Direction.DESC, "id");

    if (vSearchRequest.getPage() != null) {
      int recordsPerPage = (vSearchRequest.getRecords() != null) ? vSearchRequest.getRecords() : 10; // Default 10 records per page
      pageRequest = PageRequest.of(vSearchRequest.getPage() - 1, recordsPerPage, sorting);
    }

    Specification<CurrencyConversionHistory> spec = buildSpec(vSearchRequest);

    Page<CurrencyConversionHistory> pages = null;
    if (pageRequest != null) {
      pages = conversionHistoryRepository.findAll(spec, pageRequest);
      entities = pages.getContent();
    } else {
      entities = conversionHistoryRepository.findAll(spec, sorting);
    }

    Long totalItems = pages != null ? pages.getTotalElements() : (long) entities.size();
    Integer totalPages = pages != null ? pages.getTotalPages() : 1;
    Integer currentPage = vSearchRequest.getPage() != null ? vSearchRequest.getPage() : 1;

    if (currentPage > totalPages) {
      currentPage = 1;
      vCurrencyConversionHistoryList.clear();
    } else {
      for (CurrencyConversionHistory conversionHistory : entities) {
        vCurrencyConversionHistoryList.add(domainAdapter.fromEntityToValue(conversionHistory));
      }
    }

    log.info("Search has finished.");

    return domainAdapter.fromEntityToValue(totalItems, totalPages, currentPage, vCurrencyConversionHistoryList);
  }

  private Specification<CurrencyConversionHistory> buildSpec(VCurrencyConversionHistorySearchRequest vSearchRequest){
    Specification<CurrencyConversionHistory> spec = Specification.where(ConversionHistorySpecification.equalsTransactionId(vSearchRequest.getTransactionId()))
      .and(ConversionHistorySpecification.equalsSourceCurrency(vSearchRequest.getSourceCurrencyCode()))
      .and(ConversionHistorySpecification.equalsTargetCurrency(vSearchRequest.getTargetCurrencyCode()));

    if (vSearchRequest.getFromDate() != null && vSearchRequest.getToDate() == null){
      spec = spec.and(ConversionHistorySpecification.filterFromDate(vSearchRequest.getFromDate()));
    }else if(vSearchRequest.getToDate() != null && vSearchRequest.getFromDate() == null){
      spec = spec.and(ConversionHistorySpecification.filterToDate(vSearchRequest.getToDate()));
    }else{
      spec = spec.and(ConversionHistorySpecification.filterBetweenDates(vSearchRequest.getFromDate(),vSearchRequest.getToDate()));
    }

    return spec;
  }
}
