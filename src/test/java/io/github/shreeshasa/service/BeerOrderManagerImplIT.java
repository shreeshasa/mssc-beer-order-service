package io.github.shreeshasa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.shreeshasa.config.JmsConfig;
import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderLine;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.domain.Customer;
import io.github.shreeshasa.model.BeerDto;
import io.github.shreeshasa.model.event.AllocationFailureRequest;
import io.github.shreeshasa.model.event.DeallocateOrderRequest;
import io.github.shreeshasa.repository.BeerOrderRepository;
import io.github.shreeshasa.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author shreeshasa
 */
@ExtendWith (WireMockExtension.class)
@SpringBootTest
public class BeerOrderManagerImplIT {

  @Autowired
  BeerOrderManager beerOrderManager;

  @Autowired
  BeerOrderRepository beerOrderRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WireMockServer wireMockServer;

  @Autowired
  JmsTemplate jmsTemplate;

  Customer testCustomer;

  UUID beerId = UUID.randomUUID();

  @TestConfiguration
  static class RestTemplateBuilderProvider {

    @Bean (destroyMethod = "stop")
    public WireMockServer wireMockServer() {
      WireMockServer server = with(wireMockConfig().port(9000));
      server.start();
      return server;
    }
  }

  @BeforeEach
  void setUp() {
    testCustomer = customerRepository.save(Customer.builder()
                                               .customerName("Test Customer")
                                               .build());
  }

  @Test
  void testNewToAllocated() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.ALLOCATED, foundOrder.getOrderStatus());
    });

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
      assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
    });

    BeerOrder savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
    assertNotNull(savedBeerOrder2);
    assertEquals(BeerOrderStatus.ALLOCATED, savedBeerOrder2.getOrderStatus());
    savedBeerOrder2.getBeerOrderLines().forEach(line -> {
      assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
    });
  }

  @Test
  void testFailedValidation() throws JsonProcessingException {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("fail-validation");

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testNewToPickedUp() throws JsonProcessingException {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();

    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.ALLOCATED, foundOrder.getOrderStatus());
    });

    beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.PICKED_UP, foundOrder.getOrderStatus());
    });

    BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
    assertNotNull(pickedUpOrder);
    assertEquals(BeerOrderStatus.PICKED_UP, pickedUpOrder.getOrderStatus());
  }

  @Test
  void testAllocationFailure() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("fail-allocation");
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
    });

    AllocationFailureRequest allocationFailureRequest = (AllocationFailureRequest) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATION_FAILURE_QUEUE);
    assertNotNull(allocationFailureRequest);
    assertThat(allocationFailureRequest.getOrderId()).isEqualTo(savedBeerOrder.getId());
  }

  @Test
  void testPartialAllocation() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("partial-allocation");
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.PENDING_INVENTORY, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testValidationPendingToCanceled() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("dont-validate");
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.VALIDATION_PENDING, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.CANCELED, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testAllocationPendingToCanceled() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    beerOrder.setCustomerRef("dont-allocate");
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.ALLOCATION_PENDING, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.CANCELED, foundOrder.getOrderStatus());
    });
  }

  @Test
  void testAllocatedToCanceled() throws Exception {
    BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

    wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH + "12345")
                               .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

    BeerOrder beerOrder = createBeerOrder();
    BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.ALLOCATED, foundOrder.getOrderStatus());
    });

    beerOrderManager.cancelOrder(savedBeerOrder.getId());

    await().untilAsserted(() -> {
      BeerOrder foundOrder = beerOrderRepository.findById(savedBeerOrder.getId()).orElse(null);
      assertNotNull(foundOrder);
      assertEquals(BeerOrderStatus.CANCELED, foundOrder.getOrderStatus());
    });

    DeallocateOrderRequest deallocateOrderRequest = (DeallocateOrderRequest) jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);
    assertNotNull(deallocateOrderRequest);
    assertNotNull(deallocateOrderRequest.getBeerOrderDto());
    assertThat(deallocateOrderRequest.getBeerOrderDto().getId()).isEqualTo(savedBeerOrder.getId());
  }

  public BeerOrder createBeerOrder() {
    BeerOrder beerOrder = BeerOrder.builder()
        .customer(testCustomer)
        .build();

    Set<BeerOrderLine> lines = new HashSet<>();
    lines.add(BeerOrderLine.builder()
                  .beerId(beerId)
                  .upc("12345")
                  .orderQuantity(1)
                  .beerOrder(beerOrder)
                  .build());

    beerOrder.setBeerOrderLines(lines);

    return beerOrder;
  }
}