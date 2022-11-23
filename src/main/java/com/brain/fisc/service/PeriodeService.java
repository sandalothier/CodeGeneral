package com.brain.fisc.service;

import com.brain.fisc.service.dto.PeriodeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Periode}.
 */
public interface PeriodeService {
  /**
   * Save a periode.
   *
   * @param periodeDTO the entity to save.
   * @return the persisted entity.
   */
  PeriodeDTO save(PeriodeDTO periodeDTO);

  /**
   * Updates a periode.
   *
   * @param periodeDTO the entity to update.
   * @return the persisted entity.
   */
  PeriodeDTO update(PeriodeDTO periodeDTO);

  /**
   * Partially updates a periode.
   *
   * @param periodeDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<PeriodeDTO> partialUpdate(PeriodeDTO periodeDTO);

  /**
   * Get all the periodes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PeriodeDTO> findAll(Pageable pageable);

  /**
   * Get the "id" periode.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<PeriodeDTO> findOne(String id);

  /**
   * Delete the "id" periode.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the periode corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PeriodeDTO> search(String query, Pageable pageable);
}
