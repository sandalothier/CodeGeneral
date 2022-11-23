package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.TypeContratDeTravail;
import com.brain.fisc.repository.TypeContratDeTravailRepository;
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
 * Spring Data Elasticsearch repository for the {@link TypeContratDeTravail} entity.
 */
public interface TypeContratDeTravailSearchRepository
  extends ElasticsearchRepository<TypeContratDeTravail, String>, TypeContratDeTravailSearchRepositoryInternal {}

interface TypeContratDeTravailSearchRepositoryInternal {
  Page<TypeContratDeTravail> search(String query, Pageable pageable);

  Page<TypeContratDeTravail> search(Query query);

  void index(TypeContratDeTravail entity);
}

class TypeContratDeTravailSearchRepositoryInternalImpl implements TypeContratDeTravailSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final TypeContratDeTravailRepository repository;

  TypeContratDeTravailSearchRepositoryInternalImpl(
    ElasticsearchRestTemplate elasticsearchTemplate,
    TypeContratDeTravailRepository repository
  ) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<TypeContratDeTravail> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<TypeContratDeTravail> search(Query query) {
    SearchHits<TypeContratDeTravail> searchHits = elasticsearchTemplate.search(query, TypeContratDeTravail.class);
    List<TypeContratDeTravail> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(TypeContratDeTravail entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
