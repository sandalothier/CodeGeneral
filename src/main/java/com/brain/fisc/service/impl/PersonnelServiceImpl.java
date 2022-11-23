package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Personnel;
import com.brain.fisc.repository.PersonnelRepository;
import com.brain.fisc.repository.search.PersonnelSearchRepository;
import com.brain.fisc.service.PersonnelService;
import com.brain.fisc.service.dto.PersonnelDTO;
import com.brain.fisc.service.mapper.PersonnelMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Personnel}.
 */
@Service
public class PersonnelServiceImpl implements PersonnelService {

  private final Logger log = LoggerFactory.getLogger(PersonnelServiceImpl.class);

  private final PersonnelRepository personnelRepository;

  private final PersonnelMapper personnelMapper;

  private final PersonnelSearchRepository personnelSearchRepository;

  public PersonnelServiceImpl(
    PersonnelRepository personnelRepository,
    PersonnelMapper personnelMapper,
    PersonnelSearchRepository personnelSearchRepository
  ) {
    this.personnelRepository = personnelRepository;
    this.personnelMapper = personnelMapper;
    this.personnelSearchRepository = personnelSearchRepository;
  }

  @Override
  public PersonnelDTO save(PersonnelDTO personnelDTO) {
    log.debug("Request to save Personnel : {}", personnelDTO);
    Personnel personnel = personnelMapper.toEntity(personnelDTO);
    personnel = personnelRepository.save(personnel);
    PersonnelDTO result = personnelMapper.toDto(personnel);
    personnelSearchRepository.index(personnel);
    return result;
  }

  @Override
  public PersonnelDTO update(PersonnelDTO personnelDTO) {
    log.debug("Request to update Personnel : {}", personnelDTO);
    Personnel personnel = personnelMapper.toEntity(personnelDTO);
    personnel = personnelRepository.save(personnel);
    PersonnelDTO result = personnelMapper.toDto(personnel);
    personnelSearchRepository.index(personnel);
    return result;
  }

  @Override
  public Optional<PersonnelDTO> partialUpdate(PersonnelDTO personnelDTO) {
    log.debug("Request to partially update Personnel : {}", personnelDTO);

    return personnelRepository
      .findById(personnelDTO.getId())
      .map(existingPersonnel -> {
        personnelMapper.partialUpdate(existingPersonnel, personnelDTO);

        return existingPersonnel;
      })
      .map(personnelRepository::save)
      .map(savedPersonnel -> {
        personnelSearchRepository.save(savedPersonnel);

        return savedPersonnel;
      })
      .map(personnelMapper::toDto);
  }

  @Override
  public Page<PersonnelDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Personnel");
    return personnelRepository.findAll(pageable).map(personnelMapper::toDto);
  }

  @Override
  public Optional<PersonnelDTO> findOne(String id) {
    log.debug("Request to get Personnel : {}", id);
    return personnelRepository.findById(id).map(personnelMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Personnel : {}", id);
    personnelRepository.deleteById(id);
    personnelSearchRepository.deleteById(id);
  }

  @Override
  public Page<PersonnelDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Personnel for query {}", query);
    return personnelSearchRepository.search(query, pageable).map(personnelMapper::toDto);
  }
}
