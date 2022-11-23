package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.TypeDocument;
import com.brain.fisc.repository.TypeDocumentRepository;
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
 * Spring Data Elasticsearch repository for the {@link TypeDocument} entity.
 */
public interface TypeDocumentSearchRepository extends ElasticsearchRepository<TypeDocument, String>, TypeDocumentSearchRepositoryInternal {}

interface TypeDocumentSearchRepositoryInternal {
  Page<TypeDocument> search(String query, Pageable pageable);

  Page<TypeDocument> search(Query query);

  void index(TypeDocument entity);
}

class TypeDocumentSearchRepositoryInternalImpl implements TypeDocumentSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final TypeDocumentRepository repository;

  TypeDocumentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TypeDocumentRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<TypeDocument> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<TypeDocument> search(Query query) {
    SearchHits<TypeDocument> searchHits = elasticsearchTemplate.search(query, TypeDocument.class);
    List<TypeDocument> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(TypeDocument entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
