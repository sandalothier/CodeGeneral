package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Periode;
import com.brain.fisc.repository.PeriodeRepository;
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
 * Spring Data Elasticsearch repository for the {@link Periode} entity.
 */
public interface PeriodeSearchRepository extends ElasticsearchRepository<Periode, String>, PeriodeSearchRepositoryInternal {}

interface PeriodeSearchRepositoryInternal {
  Page<Periode> search(String query, Pageable pageable);

  Page<Periode> search(Query query);

  void index(Periode entity);
}

class PeriodeSearchRepositoryInternalImpl implements PeriodeSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final PeriodeRepository repository;

  PeriodeSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PeriodeRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Periode> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Periode> search(Query query) {
    SearchHits<Periode> searchHits = elasticsearchTemplate.search(query, Periode.class);
    List<Periode> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Periode entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
