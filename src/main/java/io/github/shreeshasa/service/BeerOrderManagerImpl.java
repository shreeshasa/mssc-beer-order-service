package io.github.shreeshasa.service;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderEvent;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.model.BeerOrderDto;
import io.github.shreeshasa.repository.BeerOrderRepository;
import io.github.shreeshasa.sm.interceptor.BeerOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {

  public static final String ORDER_ID_HEADER = "ORDER_ID";
  private final StateMachineFactory<BeerOrderStatus, BeerOrderEvent> stateMachineFactory;
  private final BeerOrderRepository beerOrderRepository;
  private final BeerOrderStateChangeInterceptor beerOrderStateChangeInterceptor;

  @Transactional
  @Override
  public BeerOrder newBeerOrder(BeerOrder beerOrder) {
    beerOrder.setId(null);
    beerOrder.setOrderStatus(BeerOrderStatus.NEW);
    BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
    sendBeerOrderEvent(savedBeerOrder, BeerOrderEvent.VALIDATE_ORDER);
    return savedBeerOrder;
  }

  @Transactional
  @Override
  public void processValidationResult(UUID beerOrderId, boolean isValid) {
    log.debug("Process Validation Result for beerOrderId: {} Valid? {}", beerOrderId, isValid);
    beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
      if (isValid) {
        sendBeerOrderEvent(beerOrder, BeerOrderEvent.VALIDATION_PASSED);

        // wait for status change
        awaitForStatus(beerOrderId, BeerOrderStatus.VALIDATED);

        beerOrderRepository.findById(beerOrderId).ifPresentOrElse(validatedBeerOrder -> {
          sendBeerOrderEvent(validatedBeerOrder, BeerOrderEvent.ALLOCATE_ORDER);
        }, () -> log.error("Order Not Found: {}", beerOrderId));
      } else {
        sendBeerOrderEvent(beerOrder, BeerOrderEvent.VALIDATION_FAILED);
      }
    }, () -> log.error("Order Not Found: {}", beerOrderId));
  }

  @Override
  public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
    beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
      sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_SUCCESS);
      // wait for status change
      awaitForStatus(beerOrder.getId(), BeerOrderStatus.ALLOCATED);

      updateAllocatedQty(beerOrderDto);
    }, () -> log.error("Order Not Found: {}", beerOrderDto.getId()));
  }

  @Override
  public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
    beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
      sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_NO_INVENTORY);

      // wait for status change
      awaitForStatus(beerOrder.getId(), BeerOrderStatus.PENDING_INVENTORY);

      updateAllocatedQty(beerOrderDto);
    }, () -> log.error("Order Not Found: {}", beerOrderDto.getId()));
  }

  @Override
  public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
    beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
      sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_FAILED);
    }, () -> log.error("Order Not Found: {}", beerOrderDto.getId()));
  }

  @Override
  public void beerOrderPickedUp(UUID id) {
    beerOrderRepository.findById(id).ifPresentOrElse(beerOrder -> {
      //do process
      sendBeerOrderEvent(beerOrder, BeerOrderEvent.ORDER_PICKED_UP);
    }, () -> log.error("Order Not Found: {}", id));
  }

  @Override
  public void cancelOrder(UUID id) {
    beerOrderRepository.findById(id).ifPresentOrElse(beerOrder -> {
      sendBeerOrderEvent(beerOrder, BeerOrderEvent.CANCEL_ORDER);
    }, () -> log.error("Order Not Found: {} ", id));
  }

  private void updateAllocatedQty(BeerOrderDto beerOrderDto) {
    beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(allocatedOrder -> {
      allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
          if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
            beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
          }
        });
      });
      beerOrderRepository.saveAndFlush(allocatedOrder);
    }, () -> log.error("Order Not Found: {}", beerOrderDto.getId()));
  }

  private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEvent event) {
    StateMachine<BeerOrderStatus, BeerOrderEvent> sm = build(beerOrder);
    Message<BeerOrderEvent> message = MessageBuilder.withPayload(event)
        .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
        .build();
    sm.sendEvent(message);
  }

  private void awaitForStatus(UUID beerOrderId, BeerOrderStatus status) {

    AtomicBoolean found = new AtomicBoolean(false);
    AtomicInteger loopCount = new AtomicInteger(0);

    while (!found.get()) {
      if (loopCount.incrementAndGet() > 10) {
        found.set(true);
        log.debug("Loop Retries exceeded");
      }

      beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
        if (beerOrder.getOrderStatus().equals(status)) {
          found.set(true);
          log.debug("Order Found");
        } else {
          log.debug("Order Status Not Equal. Expected: {} Found: {}", status.name(), beerOrder.getOrderStatus().name());
        }
      }, () -> log.error("Order Id Not Found: {}", beerOrderId));

      if (!found.get()) {
        try {
          log.debug("Sleeping for retry");
          Thread.sleep(100);
        } catch (Exception e) {
          // do nothing
        }
      }
    }
  }

  private StateMachine<BeerOrderStatus, BeerOrderEvent> build(BeerOrder beerOrder) {
    StateMachine<BeerOrderStatus, BeerOrderEvent> sm = stateMachineFactory.getStateMachine(beerOrder.getId());
    sm.stop();
    sm.getStateMachineAccessor()
        .doWithAllRegions(sma -> {
          sma.addStateMachineInterceptor(beerOrderStateChangeInterceptor);
          sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
        });
    sm.start();
    return sm;
  }
}
