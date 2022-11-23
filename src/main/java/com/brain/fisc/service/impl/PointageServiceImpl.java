package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Pointage;
import com.brain.fisc.repository.PointageRepository;
import com.brain.fisc.repository.search.PointageSearchRepository;
import com.brain.fisc.service.PointageService;
import com.brain.fisc.service.dto.PointageDTO;
import com.brain.fisc.service.mapper.PointageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Pointage}.
 */
@Service
public class PointageServiceImpl implements PointageService {

  private final Logger log = LoggerFactory.getLogger(PointageServiceImpl.class);

  private final PointageRepository pointageRepository;

  private final PointageMapper pointageMapper;

  private final PointageSearchRepository pointageSearchRepository;

  public PointageServiceImpl(
    PointageRepository pointageRepository,
    PointageMapper pointageMapper,
    PointageSearchRepository pointageSearchRepository
  ) {
    this.pointageRepository = pointageRepository;
    this.pointageMapper = pointageMapper;
    this.pointageSearchRepository = pointageSearchRepository;
  }

  @Override
  public PointageDTO save(PointageDTO pointageDTO) {
    log.debug("Request to save Pointage : {}", pointageDTO);
    Pointage pointage = pointageMapper.toEntity(pointageDTO);
    pointage = pointageRepository.save(pointage);
    PointageDTO result = pointageMapper.toDto(pointage);
    pointageSearchRepository.index(pointage);
    return result;
  }

  @Override
  public PointageDTO update(PointageDTO pointageDTO) {
    log.debug("Request to update Pointage : {}", pointageDTO);
    Pointage pointage = pointageMapper.toEntity(pointageDTO);
    pointage = pointageRepository.save(pointage);
    PointageDTO result = pointageMapper.toDto(pointage);
    pointageSearchRepository.index(pointage);
    return result;
  }

  @Override
  public Optional<PointageDTO> partialUpdate(PointageDTO pointageDTO) {
    log.debug("Request to partially update Pointage : {}", pointageDTO);

    return pointageRepository
      .findById(pointageDTO.getId())
      .map(existingPointage -> {
        pointageMapper.partialUpdate(existingPointage, pointageDTO);

        return existingPointage;
      })
      .map(pointageRepository::save)
      .map(savedPointage -> {
        pointageSearchRepository.save(savedPointage);

        return savedPointage;
      })
      .map(pointageMapper::toDto);
  }

  @Override
  public Page<PointageDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Pointages");
    return pointageRepository.findAll(pageable).map(pointageMapper::toDto);
  }

  @Override
  public Optional<PointageDTO> findOne(String id) {
    log.debug("Request to get Pointage : {}", id);
    return pointageRepository.findById(id).map(pointageMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Pointage : {}", id);
    pointageRepository.deleteById(id);
    pointageSearchRepository.deleteById(id);
  }

  @Override
  public Page<PointageDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Pointages for query {}", query);
    return pointageSearchRepository.search(query, pageable).map(pointageMapper::toDto);
  }
}
