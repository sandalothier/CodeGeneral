package com.brain.fisc.service;

import com.brain.fisc.service.dto.TraitementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Traitement}.
 */
public interface TraitementService {
  /**
   * Save a traitement.
   *
   * @param traitementDTO the entity to save.
   * @return the persisted entity.
   */
  TraitementDTO save(TraitementDTO traitementDTO);

  /**
   * Updates a traitement.
   *
   * @param traitementDTO the entity to update.
   * @return the persisted entity.
   */
  TraitementDTO update(TraitementDTO traitementDTO);

  /**
   * Partially updates a traitement.
   *
   * @param traitementDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<TraitementDTO> partialUpdate(TraitementDTO traitementDTO);

  /**
   * Get all the traitements.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TraitementDTO> findAll(Pageable pageable);

  /**
   * Get the "id" traitement.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<TraitementDTO> findOne(String id);

  /**
   * Delete the "id" traitement.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the traitement corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TraitementDTO> search(String query, Pageable pageable);
}
