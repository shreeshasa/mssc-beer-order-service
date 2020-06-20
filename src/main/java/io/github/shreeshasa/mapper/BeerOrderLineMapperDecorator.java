package io.github.shreeshasa.mapper;

import io.github.shreeshasa.domain.BeerOrderLine;
import io.github.shreeshasa.model.BeerOrderLineDto;
import io.github.shreeshasa.service.BeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author shreeshasa
 */
public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {

  @Autowired
  @Qualifier ("delegate")
  private BeerOrderLineMapper beerOrderLineMapper;

  @Autowired
  private BeerService beerService;

  @Override
  public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
    BeerOrderLineDto beerOrderLineDto = beerOrderLineMapper.beerOrderLineToDto(line);
    beerService.getBeerByUpc(line.getUpc()).ifPresent(beerDto -> {
      beerOrderLineDto.setBeerId(beerDto.getId());
      beerOrderLineDto.setBeerName(beerDto.getBeerName());
      beerOrderLineDto.setBeerStyle(beerDto.getBeerStyle());
      beerOrderLineDto.setPrice(beerDto.getPrice());
    });
    return beerOrderLineDto;
  }
}
