package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.BulletinPaie;
import com.brain.fisc.repository.BulletinPaieRepository;
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
 * Spring Data Elasticsearch repository for the {@link BulletinPaie} entity.
 */
public interface BulletinPaieSearchRepository extends ElasticsearchRepository<BulletinPaie, String>, BulletinPaieSearchRepositoryInternal {}

interface BulletinPaieSearchRepositoryInternal {
  Page<BulletinPaie> search(String query, Pageable pageable);

  Page<BulletinPaie> search(Query query);

  void index(BulletinPaie entity);
}

class BulletinPaieSearchRepositoryInternalImpl implements BulletinPaieSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final BulletinPaieRepository repository;

  BulletinPaieSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BulletinPaieRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<BulletinPaie> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<BulletinPaie> search(Query query) {
    SearchHits<BulletinPaie> searchHits = elasticsearchTemplate.search(query, BulletinPaie.class);
    List<BulletinPaie> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(BulletinPaie entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
