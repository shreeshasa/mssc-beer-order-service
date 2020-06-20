package io.github.shreeshasa.repository;

import io.github.shreeshasa.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  List<Customer> findAllByCustomerNameLike(String customerName);
}
