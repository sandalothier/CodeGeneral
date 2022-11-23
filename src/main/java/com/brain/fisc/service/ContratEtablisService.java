package com.brain.fisc.service;

import com.brain.fisc.service.dto.ContratEtablisDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.ContratEtablis}.
 */
public interface ContratEtablisService {
  /**
   * Save a contratEtablis.
   *
   * @param contratEtablisDTO the entity to save.
   * @return the persisted entity.
   */
  ContratEtablisDTO save(ContratEtablisDTO contratEtablisDTO);

  /**
   * Updates a contratEtablis.
   *
   * @param contratEtablisDTO the entity to update.
   * @return the persisted entity.
   */
  ContratEtablisDTO update(ContratEtablisDTO contratEtablisDTO);

  /**
   * Partially updates a contratEtablis.
   *
   * @param contratEtablisDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<ContratEtablisDTO> partialUpdate(ContratEtablisDTO contratEtablisDTO);

  /**
   * Get all the contratEtablis.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<ContratEtablisDTO> findAll(Pageable pageable);

  /**
   * Get the "id" contratEtablis.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<ContratEtablisDTO> findOne(String id);

  /**
   * Delete the "id" contratEtablis.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the contratEtablis corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<ContratEtablisDTO> search(String query, Pageable pageable);
}
