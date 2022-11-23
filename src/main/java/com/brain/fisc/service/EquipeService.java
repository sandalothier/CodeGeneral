package com.brain.fisc.service;

import com.brain.fisc.service.dto.EquipeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Equipe}.
 */
public interface EquipeService {
  /**
   * Save a equipe.
   *
   * @param equipeDTO the entity to save.
   * @return the persisted entity.
   */
  EquipeDTO save(EquipeDTO equipeDTO);

  /**
   * Updates a equipe.
   *
   * @param equipeDTO the entity to update.
   * @return the persisted entity.
   */
  EquipeDTO update(EquipeDTO equipeDTO);

  /**
   * Partially updates a equipe.
   *
   * @param equipeDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<EquipeDTO> partialUpdate(EquipeDTO equipeDTO);

  /**
   * Get all the equipes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<EquipeDTO> findAll(Pageable pageable);

  /**
   * Get the "id" equipe.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<EquipeDTO> findOne(String id);

  /**
   * Delete the "id" equipe.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the equipe corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<EquipeDTO> search(String query, Pageable pageable);
}
