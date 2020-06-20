package io.github.shreeshasa.service.testcomponent;

import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.model.event.ValidateOrderRequest;
import io.github.shreeshasa.model.event.ValidateOrderResult;
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
public class BeerOrderValidationListener {

  private final JmsTemplate jmsTemplate;

  @JmsListener (destination = JmsConfig.VALIDATE_ORDER_QUEUE)
  public void listen(Message message) {
    ValidateOrderRequest validateOrderRequest = (ValidateOrderRequest) message.getPayload();
    log.info("Validation Order Request: {}", validateOrderRequest);
    boolean isValid = !"fail-validation".equals(validateOrderRequest.getBeerOrderDto().getCustomerRef());
    boolean sendResponse = !"dont-validate".equals(validateOrderRequest.getBeerOrderDto().getCustomerRef());
    if (sendResponse) {
      jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE, ValidateOrderResult.builder()
          .isValid(isValid)
          .orderId(validateOrderRequest.getBeerOrderDto().getId())
          .build());
    }
  }
}
