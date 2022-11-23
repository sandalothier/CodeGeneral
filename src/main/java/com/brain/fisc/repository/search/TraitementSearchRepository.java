package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Traitement;
import com.brain.fisc.repository.TraitementRepository;
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
 * Spring Data Elasticsearch repository for the {@link Traitement} entity.
 */
public interface TraitementSearchRepository extends ElasticsearchRepository<Traitement, String>, TraitementSearchRepositoryInternal {}

interface TraitementSearchRepositoryInternal {
  Page<Traitement> search(String query, Pageable pageable);

  Page<Traitement> search(Query query);

  void index(Traitement entity);
}

class TraitementSearchRepositoryInternalImpl implements TraitementSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final TraitementRepository repository;

  TraitementSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TraitementRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Traitement> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Traitement> search(Query query) {
    SearchHits<Traitement> searchHits = elasticsearchTemplate.search(query, Traitement.class);
    List<Traitement> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Traitement entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
