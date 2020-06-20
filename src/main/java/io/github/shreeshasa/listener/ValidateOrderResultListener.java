package io.github.shreeshasa.listener;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.model.event.ValidateOrderResult;
import io.github.shreeshasa.service.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ValidateOrderResultListener {

  private final BeerOrderManager beerOrderManager;

  @JmsListener (destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
  private void listen(ValidateOrderResult validateOrderResult) {
    log.info("Validate Order Result: {}", validateOrderResult);
    final UUID beerOrderId = validateOrderResult.getOrderId();
    log.debug("Validation Result for Order Id: {}", beerOrderId);
    beerOrderManager.processValidationResult(beerOrderId, validateOrderResult.getIsValid());
  }
}
