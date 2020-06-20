package io.github.shreeshasa.service;

import io.github.shreeshasa.domain.Customer;
import io.github.shreeshasa.mapper.CustomerMapper;
import io.github.shreeshasa.model.CustomerPagedList;
import io.github.shreeshasa.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author shreeshasa
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public CustomerPagedList getCustomers(Pageable pageable) {
    Page<Customer> customerPage = customerRepository.findAll(pageable);
    return new CustomerPagedList(customerPage.stream()
                                     .map(customerMapper::customerToDto)
                                     .collect(Collectors.toList()),
                                 PageRequest.of(customerPage.getPageable().getPageNumber(), customerPage.getPageable().getPageSize()),
                                 customerPage.getTotalElements());
  }
}
