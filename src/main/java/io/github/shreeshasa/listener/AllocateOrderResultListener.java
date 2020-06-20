package io.github.shreeshasa.listener;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.model.event.AllocateOrderResult;
import io.github.shreeshasa.service.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AllocateOrderResultListener {

  private final BeerOrderManager beerOrderManager;

  @JmsListener (destination = JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE)
  public void listen(AllocateOrderResult allocateOrderResult) {
    log.info("Allocate Order Result: {}", allocateOrderResult);
    if (!allocateOrderResult.getAllocationError() && !allocateOrderResult.getPendingInventory()) {
      //allocated normally
      beerOrderManager.beerOrderAllocationPassed(allocateOrderResult.getBeerOrderDto());
    } else if (!allocateOrderResult.getAllocationError() && allocateOrderResult.getPendingInventory()) {
      //pending inventory
      beerOrderManager.beerOrderAllocationPendingInventory(allocateOrderResult.getBeerOrderDto());
    } else if (allocateOrderResult.getAllocationError()) {
      //allocation error
      beerOrderManager.beerOrderAllocationFailed(allocateOrderResult.getBeerOrderDto());
    }
  }
}
