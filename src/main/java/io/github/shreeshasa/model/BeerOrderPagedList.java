package io.github.shreeshasa.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author shreeshasa
 */
public class BeerOrderPagedList extends PageImpl<BeerOrderDto> {

  public BeerOrderPagedList(List<BeerOrderDto> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  public BeerOrderPagedList(List<BeerOrderDto> content) {
    super(content);
  }
}
