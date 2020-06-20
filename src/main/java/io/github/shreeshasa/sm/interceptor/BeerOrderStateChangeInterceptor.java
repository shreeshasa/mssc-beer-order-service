package io.github.shreeshasa.sm.interceptor;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.repository.BeerOrderRepository;
import io.github.shreeshasa.service.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEvent> {

  private final BeerOrderRepository beerOrderRepository;

  @Override
  public void preStateChange(State<BeerOrderStatus, BeerOrderEvent> state, Message<BeerOrderEvent> message, Transition<BeerOrderStatus, BeerOrderEvent> transition, StateMachine<BeerOrderStatus, BeerOrderEvent> stateMachine) {
    Optional.ofNullable(message)
        .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)))
        .ifPresent(orderId -> {
          log.debug("Saving state for order id: {} Status: {}", orderId, state.getId());
          BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(orderId)).get();
          beerOrder.setOrderStatus(state.getId());
          beerOrderRepository.saveAndFlush(beerOrder);
        });
  }
}
