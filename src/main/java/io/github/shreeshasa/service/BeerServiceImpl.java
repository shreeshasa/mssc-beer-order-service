package io.github.shreeshasa.service;

import io.github.shreeshasa.model.BeerDto;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@ConfigurationProperties (prefix = "sfg.brewery", ignoreUnknownFields = false)
@Service
public class BeerServiceImpl implements BeerService {

  public static final String BEER_PATH = "/api/v1/beer/";
  public static final String BEER_UPC_PATH = "/api/v1/beerUpc/";

  @Setter
  private String beerServiceHost;

  private final RestTemplate restTemplate;

  public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public Optional<BeerDto> getBeerById(UUID id) {
    return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_PATH + id.toString(), BeerDto.class));
  }

  @Override
  public Optional<BeerDto> getBeerByUpc(String upc) {
    return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH + upc, BeerDto.class));
  }
}
