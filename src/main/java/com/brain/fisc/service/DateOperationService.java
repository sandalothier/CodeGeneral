package com.brain.fisc.service;

import com.brain.fisc.service.dto.DateOperationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.DateOperation}.
 */
public interface DateOperationService {
  /**
   * Save a dateOperation.
   *
   * @param dateOperationDTO the entity to save.
   * @return the persisted entity.
   */
  DateOperationDTO save(DateOperationDTO dateOperationDTO);

  /**
   * Updates a dateOperation.
   *
   * @param dateOperationDTO the entity to update.
   * @return the persisted entity.
   */
  DateOperationDTO update(DateOperationDTO dateOperationDTO);

  /**
   * Partially updates a dateOperation.
   *
   * @param dateOperationDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<DateOperationDTO> partialUpdate(DateOperationDTO dateOperationDTO);

  /**
   * Get all the dateOperations.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<DateOperationDTO> findAll(Pageable pageable);

  /**
   * Get the "id" dateOperation.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<DateOperationDTO> findOne(String id);

  /**
   * Delete the "id" dateOperation.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the dateOperation corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<DateOperationDTO> search(String query, Pageable pageable);
}
