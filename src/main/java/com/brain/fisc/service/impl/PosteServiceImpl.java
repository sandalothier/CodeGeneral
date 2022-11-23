package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Poste;
import com.brain.fisc.repository.PosteRepository;
import com.brain.fisc.repository.search.PosteSearchRepository;
import com.brain.fisc.service.PosteService;
import com.brain.fisc.service.dto.PosteDTO;
import com.brain.fisc.service.mapper.PosteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Poste}.
 */
@Service
public class PosteServiceImpl implements PosteService {

  private final Logger log = LoggerFactory.getLogger(PosteServiceImpl.class);

  private final PosteRepository posteRepository;

  private final PosteMapper posteMapper;

  private final PosteSearchRepository posteSearchRepository;

  public PosteServiceImpl(PosteRepository posteRepository, PosteMapper posteMapper, PosteSearchRepository posteSearchRepository) {
    this.posteRepository = posteRepository;
    this.posteMapper = posteMapper;
    this.posteSearchRepository = posteSearchRepository;
  }

  @Override
  public PosteDTO save(PosteDTO posteDTO) {
    log.debug("Request to save Poste : {}", posteDTO);
    Poste poste = posteMapper.toEntity(posteDTO);
    poste = posteRepository.save(poste);
    PosteDTO result = posteMapper.toDto(poste);
    posteSearchRepository.index(poste);
    return result;
  }

  @Override
  public PosteDTO update(PosteDTO posteDTO) {
    log.debug("Request to update Poste : {}", posteDTO);
    Poste poste = posteMapper.toEntity(posteDTO);
    poste = posteRepository.save(poste);
    PosteDTO result = posteMapper.toDto(poste);
    posteSearchRepository.index(poste);
    return result;
  }

  @Override
  public Optional<PosteDTO> partialUpdate(PosteDTO posteDTO) {
    log.debug("Request to partially update Poste : {}", posteDTO);

    return posteRepository
      .findById(posteDTO.getId())
      .map(existingPoste -> {
        posteMapper.partialUpdate(existingPoste, posteDTO);

        return existingPoste;
      })
      .map(posteRepository::save)
      .map(savedPoste -> {
        posteSearchRepository.save(savedPoste);

        return savedPoste;
      })
      .map(posteMapper::toDto);
  }

  @Override
  public Page<PosteDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Postes");
    return posteRepository.findAll(pageable).map(posteMapper::toDto);
  }

  @Override
  public Optional<PosteDTO> findOne(String id) {
    log.debug("Request to get Poste : {}", id);
    return posteRepository.findById(id).map(posteMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Poste : {}", id);
    posteRepository.deleteById(id);
    posteSearchRepository.deleteById(id);
  }

  @Override
  public Page<PosteDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Postes for query {}", query);
    return posteSearchRepository.search(query, pageable).map(posteMapper::toDto);
  }
}
