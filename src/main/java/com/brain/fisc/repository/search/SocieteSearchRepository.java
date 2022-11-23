package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Societe;
import com.brain.fisc.repository.SocieteRepository;
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
 * Spring Data Elasticsearch repository for the {@link Societe} entity.
 */
public interface SocieteSearchRepository extends ElasticsearchRepository<Societe, String>, SocieteSearchRepositoryInternal {}

interface SocieteSearchRepositoryInternal {
  Page<Societe> search(String query, Pageable pageable);

  Page<Societe> search(Query query);

  void index(Societe entity);
}

class SocieteSearchRepositoryInternalImpl implements SocieteSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final SocieteRepository repository;

  SocieteSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SocieteRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Societe> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Societe> search(Query query) {
    SearchHits<Societe> searchHits = elasticsearchTemplate.search(query, Societe.class);
    List<Societe> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Societe entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
