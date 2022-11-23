package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Adresse;
import com.brain.fisc.repository.AdresseRepository;
import com.brain.fisc.repository.search.AdresseSearchRepository;
import com.brain.fisc.service.AdresseService;
import com.brain.fisc.service.dto.AdresseDTO;
import com.brain.fisc.service.mapper.AdresseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Adresse}.
 */
@Service
public class AdresseServiceImpl implements AdresseService {

  private final Logger log = LoggerFactory.getLogger(AdresseServiceImpl.class);

  private final AdresseRepository adresseRepository;

  private final AdresseMapper adresseMapper;

  private final AdresseSearchRepository adresseSearchRepository;

  public AdresseServiceImpl(
    AdresseRepository adresseRepository,
    AdresseMapper adresseMapper,
    AdresseSearchRepository adresseSearchRepository
  ) {
    this.adresseRepository = adresseRepository;
    this.adresseMapper = adresseMapper;
    this.adresseSearchRepository = adresseSearchRepository;
  }

  @Override
  public AdresseDTO save(AdresseDTO adresseDTO) {
    log.debug("Request to save Adresse : {}", adresseDTO);
    Adresse adresse = adresseMapper.toEntity(adresseDTO);
    adresse = adresseRepository.save(adresse);
    AdresseDTO result = adresseMapper.toDto(adresse);
    adresseSearchRepository.index(adresse);
    return result;
  }

  @Override
  public AdresseDTO update(AdresseDTO adresseDTO) {
    log.debug("Request to update Adresse : {}", adresseDTO);
    Adresse adresse = adresseMapper.toEntity(adresseDTO);
    adresse = adresseRepository.save(adresse);
    AdresseDTO result = adresseMapper.toDto(adresse);
    adresseSearchRepository.index(adresse);
    return result;
  }

  @Override
  public Optional<AdresseDTO> partialUpdate(AdresseDTO adresseDTO) {
    log.debug("Request to partially update Adresse : {}", adresseDTO);

    return adresseRepository
      .findById(adresseDTO.getId())
      .map(existingAdresse -> {
        adresseMapper.partialUpdate(existingAdresse, adresseDTO);

        return existingAdresse;
      })
      .map(adresseRepository::save)
      .map(savedAdresse -> {
        adresseSearchRepository.save(savedAdresse);

        return savedAdresse;
      })
      .map(adresseMapper::toDto);
  }

  @Override
  public Page<AdresseDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Adresses");
    return adresseRepository.findAll(pageable).map(adresseMapper::toDto);
  }

  @Override
  public Optional<AdresseDTO> findOne(String id) {
    log.debug("Request to get Adresse : {}", id);
    return adresseRepository.findById(id).map(adresseMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Adresse : {}", id);
    adresseRepository.deleteById(id);
    adresseSearchRepository.deleteById(id);
  }

  @Override
  public Page<AdresseDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Adresses for query {}", query);
    return adresseSearchRepository.search(query, pageable).map(adresseMapper::toDto);
  }
}
