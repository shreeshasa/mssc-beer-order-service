package io.github.shreeshasa.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class BeerOrderLine {

  @Id
  @GeneratedValue (generator = "UUID")
  @GenericGenerator (
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Type (type = "org.hibernate.type.UUIDCharType")
  @Column (length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
  private UUID id;

  @Version
  private Long version;

  @CreationTimestamp
  @Column (updatable = false)
  private Timestamp createdDate;

  @UpdateTimestamp
  private Timestamp lastModifiedDate;

  @ManyToOne
  private BeerOrder beerOrder;

  private UUID beerId;
  private String upc;
  private Integer orderQuantity = 0;
  private Integer quantityAllocated = 0;

  @Builder
  public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                       BeerOrder beerOrder, UUID beerId, String upc, Integer orderQuantity,
                       Integer quantityAllocated) {
    this.id = id;
    this.version = version;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.beerOrder = beerOrder;
    this.beerId = beerId;
    this.upc = upc;
    this.orderQuantity = orderQuantity;
    this.quantityAllocated = quantityAllocated;
  }
}
