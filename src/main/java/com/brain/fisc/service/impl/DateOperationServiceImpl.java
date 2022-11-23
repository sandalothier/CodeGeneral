package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.DateOperation;
import com.brain.fisc.repository.DateOperationRepository;
import com.brain.fisc.repository.search.DateOperationSearchRepository;
import com.brain.fisc.service.DateOperationService;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.service.mapper.DateOperationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link DateOperation}.
 */
@Service
public class DateOperationServiceImpl implements DateOperationService {

  private final Logger log = LoggerFactory.getLogger(DateOperationServiceImpl.class);

  private final DateOperationRepository dateOperationRepository;

  private final DateOperationMapper dateOperationMapper;

  private final DateOperationSearchRepository dateOperationSearchRepository;

  public DateOperationServiceImpl(
    DateOperationRepository dateOperationRepository,
    DateOperationMapper dateOperationMapper,
    DateOperationSearchRepository dateOperationSearchRepository
  ) {
    this.dateOperationRepository = dateOperationRepository;
    this.dateOperationMapper = dateOperationMapper;
    this.dateOperationSearchRepository = dateOperationSearchRepository;
  }

  @Override
  public DateOperationDTO save(DateOperationDTO dateOperationDTO) {
    log.debug("Request to save DateOperation : {}", dateOperationDTO);
    DateOperation dateOperation = dateOperationMapper.toEntity(dateOperationDTO);
    dateOperation = dateOperationRepository.save(dateOperation);
    DateOperationDTO result = dateOperationMapper.toDto(dateOperation);
    dateOperationSearchRepository.index(dateOperation);
    return result;
  }

  @Override
  public DateOperationDTO update(DateOperationDTO dateOperationDTO) {
    log.debug("Request to update DateOperation : {}", dateOperationDTO);
    DateOperation dateOperation = dateOperationMapper.toEntity(dateOperationDTO);
    dateOperation = dateOperationRepository.save(dateOperation);
    DateOperationDTO result = dateOperationMapper.toDto(dateOperation);
    dateOperationSearchRepository.index(dateOperation);
    return result;
  }

  @Override
  public Optional<DateOperationDTO> partialUpdate(DateOperationDTO dateOperationDTO) {
    log.debug("Request to partially update DateOperation : {}", dateOperationDTO);

    return dateOperationRepository
      .findById(dateOperationDTO.getId())
      .map(existingDateOperation -> {
        dateOperationMapper.partialUpdate(existingDateOperation, dateOperationDTO);

        return existingDateOperation;
      })
      .map(dateOperationRepository::save)
      .map(savedDateOperation -> {
        dateOperationSearchRepository.save(savedDateOperation);

        return savedDateOperation;
      })
      .map(dateOperationMapper::toDto);
  }

  @Override
  public Page<DateOperationDTO> findAll(Pageable pageable) {
    log.debug("Request to get all DateOperations");
    return dateOperationRepository.findAll(pageable).map(dateOperationMapper::toDto);
  }

  @Override
  public Optional<DateOperationDTO> findOne(String id) {
    log.debug("Request to get DateOperation : {}", id);
    return dateOperationRepository.findById(id).map(dateOperationMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete DateOperation : {}", id);
    dateOperationRepository.deleteById(id);
    dateOperationSearchRepository.deleteById(id);
  }

  @Override
  public Page<DateOperationDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of DateOperations for query {}", query);
    return dateOperationSearchRepository.search(query, pageable).map(dateOperationMapper::toDto);
  }
}
