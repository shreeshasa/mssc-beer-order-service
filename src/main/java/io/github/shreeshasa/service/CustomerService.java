package io.github.shreeshasa.service;

import io.github.shreeshasa.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

/**
 * @author shreeshasa
 */
public interface CustomerService {

  CustomerPagedList getCustomers(Pageable pageable);
}
