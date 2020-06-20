package io.github.shreeshasa.service;

import io.github.shreeshasa.model.BeerOrderDto;
import io.github.shreeshasa.model.BeerOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface BeerOrderService {

  BeerOrderPagedList getOrders(UUID customerId, Pageable pageable);

  BeerOrderDto submitOrder(UUID customerId, BeerOrderDto beerOrderDto);

  BeerOrderDto getOrderById(UUID customerId, UUID orderId);

  void pickupOrder(UUID customerId, UUID orderId);
}
