package io.github.shreeshasa.mapper;

import io.github.shreeshasa.domain.BeerOrderLine;
import io.github.shreeshasa.model.BeerOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

/**
 * @author shreeshasa
 */
@DecoratedWith (BeerOrderLineMapperDecorator.class)
@Mapper (uses = {DateMapper.class})
public interface BeerOrderLineMapper {

  BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

  BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
