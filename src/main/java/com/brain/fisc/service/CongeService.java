package com.brain.fisc.service;

import com.brain.fisc.service.dto.CongeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Conge}.
 */
public interface CongeService {
  /**
   * Save a conge.
   *
   * @param congeDTO the entity to save.
   * @return the persisted entity.
   */
  CongeDTO save(CongeDTO congeDTO);

  /**
   * Updates a conge.
   *
   * @param congeDTO the entity to update.
   * @return the persisted entity.
   */
  CongeDTO update(CongeDTO congeDTO);

  /**
   * Partially updates a conge.
   *
   * @param congeDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<CongeDTO> partialUpdate(CongeDTO congeDTO);

  /**
   * Get all the conges.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<CongeDTO> findAll(Pageable pageable);

  /**
   * Get the "id" conge.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<CongeDTO> findOne(String id);

  /**
   * Delete the "id" conge.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the conge corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<CongeDTO> search(String query, Pageable pageable);
}
