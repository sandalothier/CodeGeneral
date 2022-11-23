package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Succursale;
import com.brain.fisc.repository.SuccursaleRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Succursale} entity.
 */
public interface SuccursaleSearchRepository extends ElasticsearchRepository<Succursale, String>, SuccursaleSearchRepositoryInternal {}

interface SuccursaleSearchRepositoryInternal {
  Page<Succursale> search(String query, Pageable pageable);

  Page<Succursale> search(Query query);

  void index(Succursale entity);
}

class SuccursaleSearchRepositoryInternalImpl implements SuccursaleSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final SuccursaleRepository repository;

  SuccursaleSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SuccursaleRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Succursale> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Succursale> search(Query query) {
    SearchHits<Succursale> searchHits = elasticsearchTemplate.search(query, Succursale.class);
    List<Succursale> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Succursale entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
