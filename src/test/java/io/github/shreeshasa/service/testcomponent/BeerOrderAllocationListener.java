package io.github.shreeshasa.service.testcomponent;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.model.event.AllocateOrderRequest;
import io.github.shreeshasa.model.event.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

  private final JmsTemplate jmsTemplate;

  @JmsListener (destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
  public void listen(Message message) {
    AllocateOrderRequest allocateOrderRequest = (AllocateOrderRequest) message.getPayload();
    log.info("Allocate Order Request: {}", allocateOrderRequest);

    final boolean pendingInventory = "partial-allocation".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef());
    final boolean allocationError = "fail-allocation".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef());
    final boolean sendResponse = !"dont-allocate".equals(allocateOrderRequest.getBeerOrderDto().getCustomerRef());

    allocateOrderRequest.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
      if (pendingInventory) {
        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() - 1);
      } else {
        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
      }
    });

    if (sendResponse) {
      jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, AllocateOrderResult.builder()
          .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
          .pendingInventory(pendingInventory)
          .allocationError(allocationError)
          .build());
    }
  }
}
