package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.ComposantDocument;
import com.brain.fisc.repository.ComposantDocumentRepository;
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
 * Spring Data Elasticsearch repository for the {@link ComposantDocument} entity.
 */
public interface ComposantDocumentSearchRepository
  extends ElasticsearchRepository<ComposantDocument, String>, ComposantDocumentSearchRepositoryInternal {}

interface ComposantDocumentSearchRepositoryInternal {
  Page<ComposantDocument> search(String query, Pageable pageable);

  Page<ComposantDocument> search(Query query);

  void index(ComposantDocument entity);
}

class ComposantDocumentSearchRepositoryInternalImpl implements ComposantDocumentSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final ComposantDocumentRepository repository;

  ComposantDocumentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ComposantDocumentRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<ComposantDocument> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<ComposantDocument> search(Query query) {
    SearchHits<ComposantDocument> searchHits = elasticsearchTemplate.search(query, ComposantDocument.class);
    List<ComposantDocument> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(ComposantDocument entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
