package com.brain.fisc.service;

import com.brain.fisc.service.dto.TypeContratDeTravailDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.TypeContratDeTravail}.
 */
public interface TypeContratDeTravailService {
  /**
   * Save a typeContratDeTravail.
   *
   * @param typeContratDeTravailDTO the entity to save.
   * @return the persisted entity.
   */
  TypeContratDeTravailDTO save(TypeContratDeTravailDTO typeContratDeTravailDTO);

  /**
   * Updates a typeContratDeTravail.
   *
   * @param typeContratDeTravailDTO the entity to update.
   * @return the persisted entity.
   */
  TypeContratDeTravailDTO update(TypeContratDeTravailDTO typeContratDeTravailDTO);

  /**
   * Partially updates a typeContratDeTravail.
   *
   * @param typeContratDeTravailDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<TypeContratDeTravailDTO> partialUpdate(TypeContratDeTravailDTO typeContratDeTravailDTO);

  /**
   * Get all the typeContratDeTravails.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TypeContratDeTravailDTO> findAll(Pageable pageable);

  /**
   * Get the "id" typeContratDeTravail.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<TypeContratDeTravailDTO> findOne(String id);

  /**
   * Delete the "id" typeContratDeTravail.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the typeContratDeTravail corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TypeContratDeTravailDTO> search(String query, Pageable pageable);
}
