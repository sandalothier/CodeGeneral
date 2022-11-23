package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Societe;
import com.brain.fisc.repository.SocieteRepository;
import com.brain.fisc.repository.search.SocieteSearchRepository;
import com.brain.fisc.service.SocieteService;
import com.brain.fisc.service.dto.SocieteDTO;
import com.brain.fisc.service.mapper.SocieteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Societe}.
 */
@Service
public class SocieteServiceImpl implements SocieteService {

  private final Logger log = LoggerFactory.getLogger(SocieteServiceImpl.class);

  private final SocieteRepository societeRepository;

  private final SocieteMapper societeMapper;

  private final SocieteSearchRepository societeSearchRepository;

  public SocieteServiceImpl(
    SocieteRepository societeRepository,
    SocieteMapper societeMapper,
    SocieteSearchRepository societeSearchRepository
  ) {
    this.societeRepository = societeRepository;
    this.societeMapper = societeMapper;
    this.societeSearchRepository = societeSearchRepository;
  }

  @Override
  public SocieteDTO save(SocieteDTO societeDTO) {
    log.debug("Request to save Societe : {}", societeDTO);
    Societe societe = societeMapper.toEntity(societeDTO);
    societe = societeRepository.save(societe);
    SocieteDTO result = societeMapper.toDto(societe);
    societeSearchRepository.index(societe);
    return result;
  }

  @Override
  public SocieteDTO update(SocieteDTO societeDTO) {
    log.debug("Request to update Societe : {}", societeDTO);
    Societe societe = societeMapper.toEntity(societeDTO);
    societe = societeRepository.save(societe);
    SocieteDTO result = societeMapper.toDto(societe);
    societeSearchRepository.index(societe);
    return result;
  }

  @Override
  public Optional<SocieteDTO> partialUpdate(SocieteDTO societeDTO) {
    log.debug("Request to partially update Societe : {}", societeDTO);

    return societeRepository
      .findById(societeDTO.getId())
      .map(existingSociete -> {
        societeMapper.partialUpdate(existingSociete, societeDTO);

        return existingSociete;
      })
      .map(societeRepository::save)
      .map(savedSociete -> {
        societeSearchRepository.save(savedSociete);

        return savedSociete;
      })
      .map(societeMapper::toDto);
  }

  @Override
  public Page<SocieteDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Societes");
    return societeRepository.findAll(pageable).map(societeMapper::toDto);
  }

  @Override
  public Optional<SocieteDTO> findOne(String id) {
    log.debug("Request to get Societe : {}", id);
    return societeRepository.findById(id).map(societeMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Societe : {}", id);
    societeRepository.deleteById(id);
    societeSearchRepository.deleteById(id);
  }

  @Override
  public Page<SocieteDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Societes for query {}", query);
    return societeSearchRepository.search(query, pageable).map(societeMapper::toDto);
  }
}
