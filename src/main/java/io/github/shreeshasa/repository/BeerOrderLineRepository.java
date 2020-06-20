package io.github.shreeshasa.repository;

import io.github.shreeshasa.domain.BeerOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * @author shreeshasa
 */
public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID> {

}
