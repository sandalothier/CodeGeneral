package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.BulletinPaie;
import com.brain.fisc.repository.BulletinPaieRepository;
import com.brain.fisc.repository.search.BulletinPaieSearchRepository;
import com.brain.fisc.service.BulletinPaieService;
import com.brain.fisc.service.dto.BulletinPaieDTO;
import com.brain.fisc.service.mapper.BulletinPaieMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link BulletinPaie}.
 */
@Service
public class BulletinPaieServiceImpl implements BulletinPaieService {

  private final Logger log = LoggerFactory.getLogger(BulletinPaieServiceImpl.class);

  private final BulletinPaieRepository bulletinPaieRepository;

  private final BulletinPaieMapper bulletinPaieMapper;

  private final BulletinPaieSearchRepository bulletinPaieSearchRepository;

  public BulletinPaieServiceImpl(
    BulletinPaieRepository bulletinPaieRepository,
    BulletinPaieMapper bulletinPaieMapper,
    BulletinPaieSearchRepository bulletinPaieSearchRepository
  ) {
    this.bulletinPaieRepository = bulletinPaieRepository;
    this.bulletinPaieMapper = bulletinPaieMapper;
    this.bulletinPaieSearchRepository = bulletinPaieSearchRepository;
  }

  @Override
  public BulletinPaieDTO save(BulletinPaieDTO bulletinPaieDTO) {
    log.debug("Request to save BulletinPaie : {}", bulletinPaieDTO);
    BulletinPaie bulletinPaie = bulletinPaieMapper.toEntity(bulletinPaieDTO);
    bulletinPaie = bulletinPaieRepository.save(bulletinPaie);
    BulletinPaieDTO result = bulletinPaieMapper.toDto(bulletinPaie);
    bulletinPaieSearchRepository.index(bulletinPaie);
    return result;
  }

  @Override
  public BulletinPaieDTO update(BulletinPaieDTO bulletinPaieDTO) {
    log.debug("Request to update BulletinPaie : {}", bulletinPaieDTO);
    BulletinPaie bulletinPaie = bulletinPaieMapper.toEntity(bulletinPaieDTO);
    bulletinPaie = bulletinPaieRepository.save(bulletinPaie);
    BulletinPaieDTO result = bulletinPaieMapper.toDto(bulletinPaie);
    bulletinPaieSearchRepository.index(bulletinPaie);
    return result;
  }

  @Override
  public Optional<BulletinPaieDTO> partialUpdate(BulletinPaieDTO bulletinPaieDTO) {
    log.debug("Request to partially update BulletinPaie : {}", bulletinPaieDTO);

    return bulletinPaieRepository
      .findById(bulletinPaieDTO.getId())
      .map(existingBulletinPaie -> {
        bulletinPaieMapper.partialUpdate(existingBulletinPaie, bulletinPaieDTO);

        return existingBulletinPaie;
      })
      .map(bulletinPaieRepository::save)
      .map(savedBulletinPaie -> {
        bulletinPaieSearchRepository.save(savedBulletinPaie);

        return savedBulletinPaie;
      })
      .map(bulletinPaieMapper::toDto);
  }

  @Override
  public Page<BulletinPaieDTO> findAll(Pageable pageable) {
    log.debug("Request to get all BulletinPaies");
    return bulletinPaieRepository.findAll(pageable).map(bulletinPaieMapper::toDto);
  }

  @Override
  public Optional<BulletinPaieDTO> findOne(String id) {
    log.debug("Request to get BulletinPaie : {}", id);
    return bulletinPaieRepository.findById(id).map(bulletinPaieMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete BulletinPaie : {}", id);
    bulletinPaieRepository.deleteById(id);
    bulletinPaieSearchRepository.deleteById(id);
  }

  @Override
  public Page<BulletinPaieDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of BulletinPaies for query {}", query);
    return bulletinPaieSearchRepository.search(query, pageable).map(bulletinPaieMapper::toDto);
  }
}
