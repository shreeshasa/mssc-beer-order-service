package io.github.shreeshasa.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author shreeshasa
 */
public class CustomerPagedList extends PageImpl<CustomerDto> {

  public CustomerPagedList(List<CustomerDto> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  public CustomerPagedList(List<CustomerDto> content) {
    super(content);
  }
}
