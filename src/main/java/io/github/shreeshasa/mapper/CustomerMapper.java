package io.github.shreeshasa.mapper;

import io.github.shreeshasa.domain.Customer;
import io.github.shreeshasa.model.CustomerDto;
import org.mapstruct.Mapper;

/**
 * @author shreeshasa
 */
@Mapper (uses = {DateMapper.class})
public interface CustomerMapper {

  CustomerDto customerToDto(Customer customer);

  Customer dtoToCustomer(CustomerDto dto);
}
