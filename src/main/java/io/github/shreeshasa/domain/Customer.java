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
public class Customer {

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

  private String customerName;

  @Type (type = "org.hibernate.type.UUIDCharType")
  @Column (length = 36, columnDefinition = "varchar(36)")
  private UUID apiKey;

  @OneToMany (mappedBy = "customer")
  private Set<BeerOrder> beerOrders;

  @Builder
  public Customer(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerName,
                  UUID apiKey, Set<BeerOrder> beerOrders) {
    this.id = id;
    this.version = version;
    this.createdDate = createdDate;
    this.lastModifiedDate = lastModifiedDate;
    this.customerName = customerName;
    this.apiKey = apiKey;
    this.beerOrders = beerOrders;
  }
}
