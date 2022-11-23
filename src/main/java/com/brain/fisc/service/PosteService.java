package com.brain.fisc.service;

import com.brain.fisc.service.dto.PosteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.Poste}.
 */
public interface PosteService {
  /**
   * Save a poste.
   *
   * @param posteDTO the entity to save.
   * @return the persisted entity.
   */
  PosteDTO save(PosteDTO posteDTO);

  /**
   * Updates a poste.
   *
   * @param posteDTO the entity to update.
   * @return the persisted entity.
   */
  PosteDTO update(PosteDTO posteDTO);

  /**
   * Partially updates a poste.
   *
   * @param posteDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<PosteDTO> partialUpdate(PosteDTO posteDTO);

  /**
   * Get all the postes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PosteDTO> findAll(Pageable pageable);

  /**
   * Get the "id" poste.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<PosteDTO> findOne(String id);

  /**
   * Delete the "id" poste.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the poste corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<PosteDTO> search(String query, Pageable pageable);
}
