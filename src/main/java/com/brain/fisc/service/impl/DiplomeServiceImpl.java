package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Diplome;
import com.brain.fisc.repository.DiplomeRepository;
import com.brain.fisc.repository.search.DiplomeSearchRepository;
import com.brain.fisc.service.DiplomeService;
import com.brain.fisc.service.dto.DiplomeDTO;
import com.brain.fisc.service.mapper.DiplomeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Diplome}.
 */
@Service
public class DiplomeServiceImpl implements DiplomeService {

  private final Logger log = LoggerFactory.getLogger(DiplomeServiceImpl.class);

  private final DiplomeRepository diplomeRepository;

  private final DiplomeMapper diplomeMapper;

  private final DiplomeSearchRepository diplomeSearchRepository;

  public DiplomeServiceImpl(
    DiplomeRepository diplomeRepository,
    DiplomeMapper diplomeMapper,
    DiplomeSearchRepository diplomeSearchRepository
  ) {
    this.diplomeRepository = diplomeRepository;
    this.diplomeMapper = diplomeMapper;
    this.diplomeSearchRepository = diplomeSearchRepository;
  }

  @Override
  public DiplomeDTO save(DiplomeDTO diplomeDTO) {
    log.debug("Request to save Diplome : {}", diplomeDTO);
    Diplome diplome = diplomeMapper.toEntity(diplomeDTO);
    diplome = diplomeRepository.save(diplome);
    DiplomeDTO result = diplomeMapper.toDto(diplome);
    diplomeSearchRepository.index(diplome);
    return result;
  }

  @Override
  public DiplomeDTO update(DiplomeDTO diplomeDTO) {
    log.debug("Request to update Diplome : {}", diplomeDTO);
    Diplome diplome = diplomeMapper.toEntity(diplomeDTO);
    diplome = diplomeRepository.save(diplome);
    DiplomeDTO result = diplomeMapper.toDto(diplome);
    diplomeSearchRepository.index(diplome);
    return result;
  }

  @Override
  public Optional<DiplomeDTO> partialUpdate(DiplomeDTO diplomeDTO) {
    log.debug("Request to partially update Diplome : {}", diplomeDTO);

    return diplomeRepository
      .findById(diplomeDTO.getId())
      .map(existingDiplome -> {
        diplomeMapper.partialUpdate(existingDiplome, diplomeDTO);

        return existingDiplome;
      })
      .map(diplomeRepository::save)
      .map(savedDiplome -> {
        diplomeSearchRepository.save(savedDiplome);

        return savedDiplome;
      })
      .map(diplomeMapper::toDto);
  }

  @Override
  public Page<DiplomeDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Diplomes");
    return diplomeRepository.findAll(pageable).map(diplomeMapper::toDto);
  }

  @Override
  public Optional<DiplomeDTO> findOne(String id) {
    log.debug("Request to get Diplome : {}", id);
    return diplomeRepository.findById(id).map(diplomeMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Diplome : {}", id);
    diplomeRepository.deleteById(id);
    diplomeSearchRepository.deleteById(id);
  }

  @Override
  public Page<DiplomeDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Diplomes for query {}", query);
    return diplomeSearchRepository.search(query, pageable).map(diplomeMapper::toDto);
  }
}
