package io.github.shreeshasa.controller;

import io.github.shreeshasa.model.CustomerPagedList;
import io.github.shreeshasa.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping ("/api/v1/customers")
@RestController
public class CustomerController {

  private static final Integer DEFAULT_PAGE_NUMBER = 0;
  private static final Integer DEFAULT_PAGE_SIZE = 25;

  private final CustomerService customerService;

  @GetMapping
  public CustomerPagedList getCustomers(@RequestParam (value = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam (value = "pageSize", required = false) Integer pageSize) {
    if (pageNumber == null || pageNumber < 0) {
      pageNumber = DEFAULT_PAGE_NUMBER;
    }
    if (pageSize == null || pageSize < 0) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    return customerService.getCustomers(PageRequest.of(pageNumber, pageSize));
  }
}
