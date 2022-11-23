package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Conge;
import com.brain.fisc.repository.CongeRepository;
import com.brain.fisc.repository.search.CongeSearchRepository;
import com.brain.fisc.service.CongeService;
import com.brain.fisc.service.dto.CongeDTO;
import com.brain.fisc.service.mapper.CongeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Conge}.
 */
@Service
public class CongeServiceImpl implements CongeService {

  private final Logger log = LoggerFactory.getLogger(CongeServiceImpl.class);

  private final CongeRepository congeRepository;

  private final CongeMapper congeMapper;

  private final CongeSearchRepository congeSearchRepository;

  public CongeServiceImpl(CongeRepository congeRepository, CongeMapper congeMapper, CongeSearchRepository congeSearchRepository) {
    this.congeRepository = congeRepository;
    this.congeMapper = congeMapper;
    this.congeSearchRepository = congeSearchRepository;
  }

  @Override
  public CongeDTO save(CongeDTO congeDTO) {
    log.debug("Request to save Conge : {}", congeDTO);
    Conge conge = congeMapper.toEntity(congeDTO);
    conge = congeRepository.save(conge);
    CongeDTO result = congeMapper.toDto(conge);
    congeSearchRepository.index(conge);
    return result;
  }

  @Override
  public CongeDTO update(CongeDTO congeDTO) {
    log.debug("Request to update Conge : {}", congeDTO);
    Conge conge = congeMapper.toEntity(congeDTO);
    conge = congeRepository.save(conge);
    CongeDTO result = congeMapper.toDto(conge);
    congeSearchRepository.index(conge);
    return result;
  }

  @Override
  public Optional<CongeDTO> partialUpdate(CongeDTO congeDTO) {
    log.debug("Request to partially update Conge : {}", congeDTO);

    return congeRepository
      .findById(congeDTO.getId())
      .map(existingConge -> {
        congeMapper.partialUpdate(existingConge, congeDTO);

        return existingConge;
      })
      .map(congeRepository::save)
      .map(savedConge -> {
        congeSearchRepository.save(savedConge);

        return savedConge;
      })
      .map(congeMapper::toDto);
  }

  @Override
  public Page<CongeDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Conges");
    return congeRepository.findAll(pageable).map(congeMapper::toDto);
  }

  @Override
  public Optional<CongeDTO> findOne(String id) {
    log.debug("Request to get Conge : {}", id);
    return congeRepository.findById(id).map(congeMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Conge : {}", id);
    congeRepository.deleteById(id);
    congeSearchRepository.deleteById(id);
  }

  @Override
  public Page<CongeDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Conges for query {}", query);
    return congeSearchRepository.search(query, pageable).map(congeMapper::toDto);
  }
}
