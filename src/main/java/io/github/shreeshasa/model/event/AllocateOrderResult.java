package io.github.shreeshasa.model.event;

import io.github.shreeshasa.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shreeshasa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderResult {

  private BeerOrderDto beerOrderDto;
  private Boolean pendingInventory = false;
  private Boolean allocationError = false;
}
