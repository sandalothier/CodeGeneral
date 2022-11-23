package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Equipe;
import com.brain.fisc.repository.EquipeRepository;
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
 * Spring Data Elasticsearch repository for the {@link Equipe} entity.
 */
public interface EquipeSearchRepository extends ElasticsearchRepository<Equipe, String>, EquipeSearchRepositoryInternal {}

interface EquipeSearchRepositoryInternal {
  Page<Equipe> search(String query, Pageable pageable);

  Page<Equipe> search(Query query);

  void index(Equipe entity);
}

class EquipeSearchRepositoryInternalImpl implements EquipeSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final EquipeRepository repository;

  EquipeSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EquipeRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Equipe> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Equipe> search(Query query) {
    SearchHits<Equipe> searchHits = elasticsearchTemplate.search(query, Equipe.class);
    List<Equipe> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Equipe entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
