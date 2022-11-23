package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Traitement;
import com.brain.fisc.repository.TraitementRepository;
import com.brain.fisc.repository.search.TraitementSearchRepository;
import com.brain.fisc.service.TraitementService;
import com.brain.fisc.service.dto.TraitementDTO;
import com.brain.fisc.service.mapper.TraitementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Traitement}.
 */
@Service
public class TraitementServiceImpl implements TraitementService {

  private final Logger log = LoggerFactory.getLogger(TraitementServiceImpl.class);

  private final TraitementRepository traitementRepository;

  private final TraitementMapper traitementMapper;

  private final TraitementSearchRepository traitementSearchRepository;

  public TraitementServiceImpl(
    TraitementRepository traitementRepository,
    TraitementMapper traitementMapper,
    TraitementSearchRepository traitementSearchRepository
  ) {
    this.traitementRepository = traitementRepository;
    this.traitementMapper = traitementMapper;
    this.traitementSearchRepository = traitementSearchRepository;
  }

  @Override
  public TraitementDTO save(TraitementDTO traitementDTO) {
    log.debug("Request to save Traitement : {}", traitementDTO);
    Traitement traitement = traitementMapper.toEntity(traitementDTO);
    traitement = traitementRepository.save(traitement);
    TraitementDTO result = traitementMapper.toDto(traitement);
    traitementSearchRepository.index(traitement);
    return result;
  }

  @Override
  public TraitementDTO update(TraitementDTO traitementDTO) {
    log.debug("Request to update Traitement : {}", traitementDTO);
    Traitement traitement = traitementMapper.toEntity(traitementDTO);
    traitement = traitementRepository.save(traitement);
    TraitementDTO result = traitementMapper.toDto(traitement);
    traitementSearchRepository.index(traitement);
    return result;
  }

  @Override
  public Optional<TraitementDTO> partialUpdate(TraitementDTO traitementDTO) {
    log.debug("Request to partially update Traitement : {}", traitementDTO);

    return traitementRepository
      .findById(traitementDTO.getId())
      .map(existingTraitement -> {
        traitementMapper.partialUpdate(existingTraitement, traitementDTO);

        return existingTraitement;
      })
      .map(traitementRepository::save)
      .map(savedTraitement -> {
        traitementSearchRepository.save(savedTraitement);

        return savedTraitement;
      })
      .map(traitementMapper::toDto);
  }

  @Override
  public Page<TraitementDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Traitements");
    return traitementRepository.findAll(pageable).map(traitementMapper::toDto);
  }

  @Override
  public Optional<TraitementDTO> findOne(String id) {
    log.debug("Request to get Traitement : {}", id);
    return traitementRepository.findById(id).map(traitementMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Traitement : {}", id);
    traitementRepository.deleteById(id);
    traitementSearchRepository.deleteById(id);
  }

  @Override
  public Page<TraitementDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Traitements for query {}", query);
    return traitementSearchRepository.search(query, pageable).map(traitementMapper::toDto);
  }
}
