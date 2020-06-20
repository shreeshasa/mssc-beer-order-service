package io.github.shreeshasa.service;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.model.BeerOrderDto;

import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface BeerOrderManager {

  BeerOrder newBeerOrder(BeerOrder beerOrder);

  void processValidationResult(UUID beerOrderId, boolean isValid);

  void beerOrderAllocationPassed(BeerOrderDto beerOrderDto);

  void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto);

  void beerOrderAllocationFailed(BeerOrderDto beerOrderDto);

  void beerOrderPickedUp(UUID id);

  void cancelOrder(UUID id);
}
