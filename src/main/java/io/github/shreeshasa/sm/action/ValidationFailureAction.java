package io.github.shreeshasa.sm.action;

import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.service.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationFailureAction implements Action<BeerOrderStatus, BeerOrderEvent> {

  @Override
  public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
    String beerOrderId = (String) context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
    log.error("Validation Failed: {}", beerOrderId);
  }
}
