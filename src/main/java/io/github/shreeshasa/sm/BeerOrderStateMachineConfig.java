package io.github.shreeshasa.sm;

import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * @author shreeshasa
 */
@RequiredArgsConstructor
@EnableStateMachineFactory
@Configuration
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEvent> {

  private final Action<BeerOrderStatus, BeerOrderEvent> validateOrderAction;
  private final Action<BeerOrderStatus, BeerOrderEvent> allocateOrderAction;
  private final Action<BeerOrderStatus, BeerOrderEvent> validationFailureAction;
  private final Action<BeerOrderStatus, BeerOrderEvent> allocationFailureAction;
  private final Action<BeerOrderStatus, BeerOrderEvent> deallocateOrderAction;

  @Override
  public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEvent> states) throws Exception {
    states.withStates()
        .initial(BeerOrderStatus.NEW)
        .states(EnumSet.allOf(BeerOrderStatus.class))
        .end(BeerOrderStatus.PICKED_UP)
        .end(BeerOrderStatus.DELIVERED)
        .end(BeerOrderStatus.CANCELED)
        .end(BeerOrderStatus.DELIVERY_EXCEPTION)
        .end(BeerOrderStatus.VALIDATION_EXCEPTION)
        .end(BeerOrderStatus.ALLOCATION_EXCEPTION);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEvent> transitions) throws Exception {
    transitions.withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATION_PENDING).event(BeerOrderEvent.VALIDATE_ORDER).action(validateOrderAction)
        .and()
        .withExternal().source(BeerOrderStatus.VALIDATION_PENDING).target(BeerOrderStatus.VALIDATED).event(BeerOrderEvent.VALIDATION_PASSED)
        .and()
        .withExternal().source(BeerOrderStatus.VALIDATION_PENDING).target(BeerOrderStatus.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
        .and()
        .withExternal().source(BeerOrderStatus.VALIDATION_PENDING).target(BeerOrderStatus.VALIDATION_EXCEPTION).event(BeerOrderEvent.VALIDATION_FAILED).action(validationFailureAction)
        .and()
        .withExternal().source(BeerOrderStatus.VALIDATED).target(BeerOrderStatus.ALLOCATION_PENDING).event(BeerOrderEvent.ALLOCATE_ORDER).action(allocateOrderAction)
        .and()
        .withExternal().source(BeerOrderStatus.VALIDATED).target(BeerOrderStatus.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.ALLOCATED).event(BeerOrderEvent.ALLOCATION_SUCCESS)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.ALLOCATION_EXCEPTION).event(BeerOrderEvent.ALLOCATION_FAILED).action(allocationFailureAction)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.CANCELED).event(BeerOrderEvent.CANCEL_ORDER)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATION_PENDING).target(BeerOrderStatus.PENDING_INVENTORY).event(BeerOrderEvent.ALLOCATION_NO_INVENTORY)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATED).target(BeerOrderStatus.PICKED_UP).event(BeerOrderEvent.ORDER_PICKED_UP)
        .and()
        .withExternal().source(BeerOrderStatus.ALLOCATED).target(BeerOrderStatus.CANCELED).event(BeerOrderEvent.CANCEL_ORDER).action(deallocateOrderAction)
    ;
  }
}
