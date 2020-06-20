package io.github.shreeshasa.mapper;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.model.BeerOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author shreeshasa
 */
@Mapper (uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

  @Mapping (target = "customerId", source = "customer.id")
  BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

  BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}
