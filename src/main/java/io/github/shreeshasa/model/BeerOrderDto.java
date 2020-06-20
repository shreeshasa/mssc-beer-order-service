package io.github.shreeshasa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrderDto {

  private UUID id;
  private Integer version;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime createdDate;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime lastModifiedDate;

  private UUID customerId;
  private String customerRef;
  private List<BeerOrderLineDto> beerOrderLines;
  private String orderStatus;
  private String orderStatusCallbackUrl;
}
