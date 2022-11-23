package com.brain.fisc.service;

import com.brain.fisc.service.dto.DiplomeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Diplome}.
 */
public interface DiplomeService {
  /**
   * Save a diplome.
   *
   * @param diplomeDTO the entity to save.
   * @return the persisted entity.
   */
  DiplomeDTO save(DiplomeDTO diplomeDTO);

  /**
   * Updates a diplome.
   *
   * @param diplomeDTO the entity to update.
   * @return the persisted entity.
   */
  DiplomeDTO update(DiplomeDTO diplomeDTO);

  /**
   * Partially updates a diplome.
   *
   * @param diplomeDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<DiplomeDTO> partialUpdate(DiplomeDTO diplomeDTO);

  /**
   * Get all the diplomes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<DiplomeDTO> findAll(Pageable pageable);

  /**
   * Get the "id" diplome.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<DiplomeDTO> findOne(String id);

  /**
   * Delete the "id" diplome.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the diplome corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<DiplomeDTO> search(String query, Pageable pageable);
}
