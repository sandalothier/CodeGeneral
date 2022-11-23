package com.brain.fisc.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.brain.fisc.domain.Personnel;
import com.brain.fisc.repository.PersonnelRepository;
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
 * Spring Data Elasticsearch repository for the {@link Personnel} entity.
 */
public interface PersonnelSearchRepository extends ElasticsearchRepository<Personnel, String>, PersonnelSearchRepositoryInternal {}

interface PersonnelSearchRepositoryInternal {
  Page<Personnel> search(String query, Pageable pageable);

  Page<Personnel> search(Query query);

  void index(Personnel entity);
}

class PersonnelSearchRepositoryInternalImpl implements PersonnelSearchRepositoryInternal {

  private final ElasticsearchRestTemplate elasticsearchTemplate;
  private final PersonnelRepository repository;

  PersonnelSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PersonnelRepository repository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.repository = repository;
  }

  @Override
  public Page<Personnel> search(String query, Pageable pageable) {
    NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
    return search(nativeSearchQuery.setPageable(pageable));
  }

  @Override
  public Page<Personnel> search(Query query) {
    SearchHits<Personnel> searchHits = elasticsearchTemplate.search(query, Personnel.class);
    List<Personnel> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
    return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
  }

  @Override
  public void index(Personnel entity) {
    repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
  }
}
