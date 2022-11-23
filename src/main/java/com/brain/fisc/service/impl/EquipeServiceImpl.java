package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Equipe;
import com.brain.fisc.repository.EquipeRepository;
import com.brain.fisc.repository.search.EquipeSearchRepository;
import com.brain.fisc.service.EquipeService;
import com.brain.fisc.service.dto.EquipeDTO;
import com.brain.fisc.service.mapper.EquipeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Equipe}.
 */
@Service
public class EquipeServiceImpl implements EquipeService {

  private final Logger log = LoggerFactory.getLogger(EquipeServiceImpl.class);

  private final EquipeRepository equipeRepository;

  private final EquipeMapper equipeMapper;

  private final EquipeSearchRepository equipeSearchRepository;

  public EquipeServiceImpl(EquipeRepository equipeRepository, EquipeMapper equipeMapper, EquipeSearchRepository equipeSearchRepository) {
    this.equipeRepository = equipeRepository;
    this.equipeMapper = equipeMapper;
    this.equipeSearchRepository = equipeSearchRepository;
  }

  @Override
  public EquipeDTO save(EquipeDTO equipeDTO) {
    log.debug("Request to save Equipe : {}", equipeDTO);
    Equipe equipe = equipeMapper.toEntity(equipeDTO);
    equipe = equipeRepository.save(equipe);
    EquipeDTO result = equipeMapper.toDto(equipe);
    equipeSearchRepository.index(equipe);
    return result;
  }

  @Override
  public EquipeDTO update(EquipeDTO equipeDTO) {
    log.debug("Request to update Equipe : {}", equipeDTO);
    Equipe equipe = equipeMapper.toEntity(equipeDTO);
    equipe = equipeRepository.save(equipe);
    EquipeDTO result = equipeMapper.toDto(equipe);
    equipeSearchRepository.index(equipe);
    return result;
  }

  @Override
  public Optional<EquipeDTO> partialUpdate(EquipeDTO equipeDTO) {
    log.debug("Request to partially update Equipe : {}", equipeDTO);

    return equipeRepository
      .findById(equipeDTO.getId())
      .map(existingEquipe -> {
        equipeMapper.partialUpdate(existingEquipe, equipeDTO);

        return existingEquipe;
      })
      .map(equipeRepository::save)
      .map(savedEquipe -> {
        equipeSearchRepository.save(savedEquipe);

        return savedEquipe;
      })
      .map(equipeMapper::toDto);
  }

  @Override
  public Page<EquipeDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Equipes");
    return equipeRepository.findAll(pageable).map(equipeMapper::toDto);
  }

  @Override
  public Optional<EquipeDTO> findOne(String id) {
    log.debug("Request to get Equipe : {}", id);
    return equipeRepository.findById(id).map(equipeMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Equipe : {}", id);
    equipeRepository.deleteById(id);
    equipeSearchRepository.deleteById(id);
  }

  @Override
  public Page<EquipeDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Equipes for query {}", query);
    return equipeSearchRepository.search(query, pageable).map(equipeMapper::toDto);
  }
}
