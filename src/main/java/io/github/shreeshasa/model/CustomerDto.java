package io.github.shreeshasa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Data
public class CustomerDto {

  private UUID id;
  private Integer version;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime createdDate;

  @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
  private OffsetDateTime lastModifiedDate;

  private String customerName;
}
