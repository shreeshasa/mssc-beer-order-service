package io.github.shreeshasa.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author shreeshasa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationFailureRequest {

  private UUID orderId;
}
