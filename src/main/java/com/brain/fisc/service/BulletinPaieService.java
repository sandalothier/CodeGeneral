package com.brain.fisc.service;

import com.brain.fisc.service.dto.BulletinPaieDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.BulletinPaie}.
 */
public interface BulletinPaieService {
  /**
   * Save a bulletinPaie.
   *
   * @param bulletinPaieDTO the entity to save.
   * @return the persisted entity.
   */
  BulletinPaieDTO save(BulletinPaieDTO bulletinPaieDTO);

  /**
   * Updates a bulletinPaie.
   *
   * @param bulletinPaieDTO the entity to update.
   * @return the persisted entity.
   */
  BulletinPaieDTO update(BulletinPaieDTO bulletinPaieDTO);

  /**
   * Partially updates a bulletinPaie.
   *
   * @param bulletinPaieDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<BulletinPaieDTO> partialUpdate(BulletinPaieDTO bulletinPaieDTO);

  /**
   * Get all the bulletinPaies.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<BulletinPaieDTO> findAll(Pageable pageable);

  /**
   * Get the "id" bulletinPaie.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<BulletinPaieDTO> findOne(String id);

  /**
   * Delete the "id" bulletinPaie.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the bulletinPaie corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<BulletinPaieDTO> search(String query, Pageable pageable);
}
