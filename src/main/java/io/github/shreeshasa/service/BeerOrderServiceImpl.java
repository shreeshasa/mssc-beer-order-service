package io.github.shreeshasa.service;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.domain.Customer;
import io.github.shreeshasa.mapper.BeerOrderMapper;
import io.github.shreeshasa.model.BeerOrderDto;
import io.github.shreeshasa.model.BeerOrderPagedList;
import io.github.shreeshasa.repository.BeerOrderRepository;
import io.github.shreeshasa.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

  private final BeerOrderRepository beerOrderRepository;
  private final CustomerRepository customerRepository;
  private final BeerOrderMapper beerOrderMapper;
  private final BeerOrderManager beerOrderManager;

  @Override
  public BeerOrderPagedList getOrders(UUID customerId, Pageable pageable) {
    Customer customer = customerRepository.findById(customerId).orElse(null);
    if (null != customer) {
      Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customer, pageable);
      return new BeerOrderPagedList(beerOrderPage.stream().map(beerOrderMapper::beerOrderToDto).collect(Collectors.toList()),
                                    PageRequest.of(beerOrderPage.getPageable().getPageNumber(), beerOrderPage.getPageable().getPageSize()),
                                    beerOrderPage.getTotalElements());
    }
    return null;
  }

  @Transactional
  @Override
  public BeerOrderDto submitOrder(UUID customerId, BeerOrderDto beerOrderDto) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer Not Found"));
    BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
    beerOrder.setId(null); //should not be set by outside client
    beerOrder.setCustomer(customer);
    beerOrder.setOrderStatus(BeerOrderStatus.NEW);

    beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
    log.debug("Saved Beer Order: {}", beerOrder.getId());

    return beerOrderMapper.beerOrderToDto(savedBeerOrder);
  }

  @Override
  public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
    return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
  }

  @Override
  public void pickupOrder(UUID customerId, UUID orderId) {
    BeerOrder beerOrder = getOrder(customerId, orderId);
    beerOrder.setOrderStatus(BeerOrderStatus.PICKED_UP);
    beerOrderRepository.save(beerOrder);
  }

  private BeerOrder getOrder(UUID customerId, UUID orderId) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer Not Found"));
    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);
    if (beerOrderOptional.isPresent()) {
      BeerOrder beerOrder = beerOrderOptional.get();
      // fall to exception if customer id's do not match - order not for customer
      if (beerOrder.getCustomer().getId().equals(customerId)) {
        return beerOrder;
      }
    }
    throw new RuntimeException("Beer Order Not Found");
  }
}
