package io.github.shreeshasa.sm.action;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.model.event.AllocationFailureRequest;
import io.github.shreeshasa.service.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<BeerOrderStatus, BeerOrderEvent> {

  private final JmsTemplate jmsTemplate;

  @Override
  public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
    String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
    jmsTemplate.convertAndSend(JmsConfig.ALLOCATION_FAILURE_QUEUE, AllocationFailureRequest.builder()
        .orderId(UUID.fromString(beerOrderId))
        .build());
    log.debug("Sent Allocation Failure to queue for order id {}", beerOrderId);
  }
}
