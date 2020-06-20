package io.github.shreeshasa.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

/**
 * @author shreeshasa
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class BeerOrder {

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

  private String customerRef;

  @ManyToOne
  private Customer customer;

  @OneToMany (mappedBy = "beerOrder", cascade = CascadeType.ALL)
  @Fetch (FetchMode.JOIN)
  private Set<BeerOrderLine> beerOrderLines;

  private BeerOrderStatus orderStatus = BeerOrderStatus.NEW;
  private String orderStatusCallbackUrl;

  @Builder
  public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef, Customer customer,
                   Set<BeerOrderLine> beerOrderLines, BeerOrderStatus orderStatus,
                   String orderStatusCallbackUrl) {
    this.id = id;
    this.version = version;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.customerRef = customerRef;
    this.customer = customer;
    this.beerOrderLines = beerOrderLines;
    this.orderStatus = orderStatus;
    this.orderStatusCallbackUrl = orderStatusCallbackUrl;
  }
}
