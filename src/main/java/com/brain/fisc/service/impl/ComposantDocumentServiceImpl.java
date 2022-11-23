package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.ComposantDocument;
import com.brain.fisc.repository.ComposantDocumentRepository;
import com.brain.fisc.repository.search.ComposantDocumentSearchRepository;
import com.brain.fisc.service.ComposantDocumentService;
import com.brain.fisc.service.dto.ComposantDocumentDTO;
import com.brain.fisc.service.mapper.ComposantDocumentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link ComposantDocument}.
 */
@Service
public class ComposantDocumentServiceImpl implements ComposantDocumentService {

  private final Logger log = LoggerFactory.getLogger(ComposantDocumentServiceImpl.class);

  private final ComposantDocumentRepository composantDocumentRepository;

  private final ComposantDocumentMapper composantDocumentMapper;

  private final ComposantDocumentSearchRepository composantDocumentSearchRepository;

  public ComposantDocumentServiceImpl(
    ComposantDocumentRepository composantDocumentRepository,
    ComposantDocumentMapper composantDocumentMapper,
    ComposantDocumentSearchRepository composantDocumentSearchRepository
  ) {
    this.composantDocumentRepository = composantDocumentRepository;
    this.composantDocumentMapper = composantDocumentMapper;
    this.composantDocumentSearchRepository = composantDocumentSearchRepository;
  }

  @Override
  public ComposantDocumentDTO save(ComposantDocumentDTO composantDocumentDTO) {
    log.debug("Request to save ComposantDocument : {}", composantDocumentDTO);
    ComposantDocument composantDocument = composantDocumentMapper.toEntity(composantDocumentDTO);
    composantDocument = composantDocumentRepository.save(composantDocument);
    ComposantDocumentDTO result = composantDocumentMapper.toDto(composantDocument);
    composantDocumentSearchRepository.index(composantDocument);
    return result;
  }

  @Override
  public ComposantDocumentDTO update(ComposantDocumentDTO composantDocumentDTO) {
    log.debug("Request to update ComposantDocument : {}", composantDocumentDTO);
    ComposantDocument composantDocument = composantDocumentMapper.toEntity(composantDocumentDTO);
    composantDocument = composantDocumentRepository.save(composantDocument);
    ComposantDocumentDTO result = composantDocumentMapper.toDto(composantDocument);
    composantDocumentSearchRepository.index(composantDocument);
    return result;
  }

  @Override
  public Optional<ComposantDocumentDTO> partialUpdate(ComposantDocumentDTO composantDocumentDTO) {
    log.debug("Request to partially update ComposantDocument : {}", composantDocumentDTO);

    return composantDocumentRepository
      .findById(composantDocumentDTO.getId())
      .map(existingComposantDocument -> {
        composantDocumentMapper.partialUpdate(existingComposantDocument, composantDocumentDTO);

        return existingComposantDocument;
      })
      .map(composantDocumentRepository::save)
      .map(savedComposantDocument -> {
        composantDocumentSearchRepository.save(savedComposantDocument);

        return savedComposantDocument;
      })
      .map(composantDocumentMapper::toDto);
  }

  @Override
  public Page<ComposantDocumentDTO> findAll(Pageable pageable) {
    log.debug("Request to get all ComposantDocuments");
    return composantDocumentRepository.findAll(pageable).map(composantDocumentMapper::toDto);
  }

  @Override
  public Optional<ComposantDocumentDTO> findOne(String id) {
    log.debug("Request to get ComposantDocument : {}", id);
    return composantDocumentRepository.findById(id).map(composantDocumentMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete ComposantDocument : {}", id);
    composantDocumentRepository.deleteById(id);
    composantDocumentSearchRepository.deleteById(id);
  }

  @Override
  public Page<ComposantDocumentDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of ComposantDocuments for query {}", query);
    return composantDocumentSearchRepository.search(query, pageable).map(composantDocumentMapper::toDto);
  }
}
