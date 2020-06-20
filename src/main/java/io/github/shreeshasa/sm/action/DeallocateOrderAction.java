package io.github.shreeshasa.sm.action;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.mapper.BeerOrderMapper;
import io.github.shreeshasa.model.event.DeallocateOrderRequest;
import io.github.shreeshasa.repository.BeerOrderRepository;
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
public class DeallocateOrderAction implements Action<BeerOrderStatus, BeerOrderEvent> {

  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;
  private final JmsTemplate jmsTemplate;

  @Override
  public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
    String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
    beerOrderRepository.findById(UUID.fromString(beerOrderId)).ifPresentOrElse(beerOrder -> {
      jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE, DeallocateOrderRequest.builder()
          .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
          .build());
      log.debug("Sent Deallocation Request to queue for order id {}", beerOrder);
    }, () -> log.error("Beer Order Not Found: {}", beerOrderId));
  }
}
