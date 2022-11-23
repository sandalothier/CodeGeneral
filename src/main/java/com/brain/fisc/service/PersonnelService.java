package com.brain.fisc.service;

import com.brain.fisc.service.dto.PersonnelDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Personnel}.
 */
public interface PersonnelService {
  /**
   * Save a personnel.
   *
   * @param personnelDTO the entity to save.
   * @return the persisted entity.
   */
  PersonnelDTO save(PersonnelDTO personnelDTO);

  /**
   * Updates a personnel.
   *
   * @param personnelDTO the entity to update.
   * @return the persisted entity.
   */
  PersonnelDTO update(PersonnelDTO personnelDTO);

  /**
   * Partially updates a personnel.
   *
   * @param personnelDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<PersonnelDTO> partialUpdate(PersonnelDTO personnelDTO);

  /**
   * Get all the personnel.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PersonnelDTO> findAll(Pageable pageable);

  /**
   * Get the "id" personnel.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<PersonnelDTO> findOne(String id);

  /**
   * Delete the "id" personnel.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the personnel corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PersonnelDTO> search(String query, Pageable pageable);
}
