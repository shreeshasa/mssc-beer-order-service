package io.github.shreeshasa.controller;

import io.github.shreeshasa.model.BeerOrderDto;
import io.github.shreeshasa.model.BeerOrderPagedList;
import io.github.shreeshasa.service.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author shreeshasa
 */
@RequiredArgsConstructor
@RequestMapping ("/api/v1/customers/{customerId}/orders")
@RestController
public class BeerOrderController {

  private static final Integer DEFAULT_PAGE_NUMBER = 0;
  private static final Integer DEFAULT_PAGE_SIZE = 25;

  private final BeerOrderService beerOrderService;

  @GetMapping
  public BeerOrderPagedList getOrders(@PathVariable ("customerId") UUID customerId,
                                       @RequestParam (value = "pageNumber", required = false) Integer pageNumber,
                                       @RequestParam (value = "pageSize", required = false) Integer pageSize) {
    if (pageNumber == null || pageNumber < 0) {
      pageNumber = DEFAULT_PAGE_NUMBER;
    }

    if (pageSize == null || pageSize < 1) {
      pageSize = DEFAULT_PAGE_SIZE;
    }

    return beerOrderService.getOrders(customerId, PageRequest.of(pageNumber, pageSize));
  }

  @PostMapping
  @ResponseStatus (HttpStatus.CREATED)
  public BeerOrderDto submitOrder(@PathVariable ("customerId") UUID customerId,
                                  @RequestBody BeerOrderDto beerOrderDto) {
    return beerOrderService.submitOrder(customerId, beerOrderDto);
  }

  @GetMapping ("/{orderId}")
  public BeerOrderDto getOrder(@PathVariable ("customerId") UUID customerId,
                               @PathVariable ("orderId") UUID orderId) {
    return beerOrderService.getOrderById(customerId, orderId);
  }

  @PutMapping ("/{orderId}/pickup")
  @ResponseStatus (HttpStatus.NO_CONTENT)
  public void pickupOrder(@PathVariable ("customerId") UUID customerId,
                          @PathVariable ("orderId") UUID orderId) {
    beerOrderService.pickupOrder(customerId, orderId);
  }
}
