package io.github.shreeshasa.repository;

import io.github.shreeshasa.domain.BeerOrder;
import io.github.shreeshasa.domain.BeerOrderStatus;
import io.github.shreeshasa.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

  Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

  List<BeerOrder> findAllByOrderStatus(BeerOrderStatus orderStatusEnum);

//  @Lock (LockModeType.PESSIMISTIC_WRITE)
//  BeerOrder findOneById(UUID id);
}
