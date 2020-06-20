package io.github.shreeshasa.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * @author shreeshasa
 */
public class BeerPagedList extends PageImpl<BeerDto> implements Serializable {

  private static final long serialVersionUID = 2166427509516529197L;

  public BeerPagedList(List<BeerDto> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  public BeerPagedList(List<BeerDto> content) {
    super(content);
  }
}
