package io.github.shreeshasa.sm.action;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.mapper.BeerOrderMapper;
import io.github.shreeshasa.model.event.ValidateOrderRequest;
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
public class ValidateOrderAction implements Action<BeerOrderStatus, BeerOrderEvent> {

  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderMapper beerOrderMapper;
  private final JmsTemplate jmsTemplate;

  @Override
  public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
    String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
    BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(beerOrderId)).orElse(null);
    jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
        .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder)).build());
    log.debug("Sent Validation Request to queue for order id {}", beerOrder);
  }
}
