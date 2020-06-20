package io.github.shreeshasa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Data
@Builder
public class BeerOrderLineDto {

  private UUID id;
  private Integer version;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime createdDate;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime lastModifiedDate;

  private String upc;
  private String beerName;
  private String beerStyle;
  private UUID beerId;
  private Integer orderQuantity = 0;
  private BigDecimal price;
  private Integer quantityAllocated;
}
