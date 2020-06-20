package io.github.shreeshasa.service;

import io.github.shreeshasa.bootstrap.BeerOrderBootStrap;
import io.github.shreeshasa.domain.Customer;
import io.github.shreeshasa.model.BeerOrderDto;
import io.github.shreeshasa.model.BeerOrderLineDto;
import io.github.shreeshasa.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TastingRoomService {

  private final CustomerRepository customerRepository;
  private final BeerOrderService beerOrderService;

  private final List<String> beerUpcs = Arrays.asList(BeerOrderBootStrap.BEER_1_UPC,
                                                      BeerOrderBootStrap.BEER_2_UPC,
                                                      BeerOrderBootStrap.BEER_3_UPC);

  @Transactional
  @Scheduled (fixedRate = 2000) // run every 2 seconds
  public void placeTastingRoomOrder() {
    List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);
    if (customerList.size() == 1) { //should be just one
      submitOrder(customerList.get(0));
    } else {
      log.error("Too many or too few tasting room customers found");
    }
  }

  private void submitOrder(Customer customer) {
    String beerToOrder = getRandomBeerUpc();

    BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
        .upc(beerToOrder)
        .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
        .build();

    List<BeerOrderLineDto> beerOrderLineSet = new ArrayList<>();
    beerOrderLineSet.add(beerOrderLine);

    BeerOrderDto beerOrder = BeerOrderDto.builder()
        .customerId(customer.getId())
        .customerRef(UUID.randomUUID().toString())
        .beerOrderLines(beerOrderLineSet)
        .build();

    BeerOrderDto savedOrder = beerOrderService.submitOrder(customer.getId(), beerOrder);
  }

  private String getRandomBeerUpc() {
    return beerUpcs.get(new Random().nextInt(beerUpcs.size() - 0));
  }
}
