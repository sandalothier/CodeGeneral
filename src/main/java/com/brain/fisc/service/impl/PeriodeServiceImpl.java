package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Periode;
import com.brain.fisc.repository.PeriodeRepository;
import com.brain.fisc.repository.search.PeriodeSearchRepository;
import com.brain.fisc.service.PeriodeService;
import com.brain.fisc.service.dto.PeriodeDTO;
import com.brain.fisc.service.mapper.PeriodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Periode}.
 */
@Service
public class PeriodeServiceImpl implements PeriodeService {

  private final Logger log = LoggerFactory.getLogger(PeriodeServiceImpl.class);

  private final PeriodeRepository periodeRepository;

  private final PeriodeMapper periodeMapper;

  private final PeriodeSearchRepository periodeSearchRepository;

  public PeriodeServiceImpl(
    PeriodeRepository periodeRepository,
    PeriodeMapper periodeMapper,
    PeriodeSearchRepository periodeSearchRepository
  ) {
    this.periodeRepository = periodeRepository;
    this.periodeMapper = periodeMapper;
    this.periodeSearchRepository = periodeSearchRepository;
  }

  @Override
  public PeriodeDTO save(PeriodeDTO periodeDTO) {
    log.debug("Request to save Periode : {}", periodeDTO);
    Periode periode = periodeMapper.toEntity(periodeDTO);
    periode = periodeRepository.save(periode);
    PeriodeDTO result = periodeMapper.toDto(periode);
    periodeSearchRepository.index(periode);
    return result;
  }

  @Override
  public PeriodeDTO update(PeriodeDTO periodeDTO) {
    log.debug("Request to update Periode : {}", periodeDTO);
    Periode periode = periodeMapper.toEntity(periodeDTO);
    periode = periodeRepository.save(periode);
    PeriodeDTO result = periodeMapper.toDto(periode);
    periodeSearchRepository.index(periode);
    return result;
  }

  @Override
  public Optional<PeriodeDTO> partialUpdate(PeriodeDTO periodeDTO) {
    log.debug("Request to partially update Periode : {}", periodeDTO);

    return periodeRepository
      .findById(periodeDTO.getId())
      .map(existingPeriode -> {
        periodeMapper.partialUpdate(existingPeriode, periodeDTO);

        return existingPeriode;
      })
      .map(periodeRepository::save)
      .map(savedPeriode -> {
        periodeSearchRepository.save(savedPeriode);

        return savedPeriode;
      })
      .map(periodeMapper::toDto);
  }

  @Override
  public Page<PeriodeDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Periodes");
    return periodeRepository.findAll(pageable).map(periodeMapper::toDto);
  }

  @Override
  public Optional<PeriodeDTO> findOne(String id) {
    log.debug("Request to get Periode : {}", id);
    return periodeRepository.findById(id).map(periodeMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Periode : {}", id);
    periodeRepository.deleteById(id);
    periodeSearchRepository.deleteById(id);
  }

  @Override
  public Page<PeriodeDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Periodes for query {}", query);
    return periodeSearchRepository.search(query, pageable).map(periodeMapper::toDto);
  }
}
