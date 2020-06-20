package io.github.shreeshasa.service;

import io.github.shreeshasa.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface BeerService {

  Optional<BeerDto> getBeerById(UUID id);

  Optional<BeerDto> getBeerByUpc(String upc);
}
