package com.brain.fisc.service;

import com.brain.fisc.service.dto.ComposantDocumentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.ComposantDocument}.
 */
public interface ComposantDocumentService {
  /**
   * Save a composantDocument.
   *
   * @param composantDocumentDTO the entity to save.
   * @return the persisted entity.
   */
  ComposantDocumentDTO save(ComposantDocumentDTO composantDocumentDTO);

  /**
   * Updates a composantDocument.
   *
   * @param composantDocumentDTO the entity to update.
   * @return the persisted entity.
   */
  ComposantDocumentDTO update(ComposantDocumentDTO composantDocumentDTO);

  /**
   * Partially updates a composantDocument.
   *
   * @param composantDocumentDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<ComposantDocumentDTO> partialUpdate(ComposantDocumentDTO composantDocumentDTO);

  /**
   * Get all the composantDocuments.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<ComposantDocumentDTO> findAll(Pageable pageable);

  /**
   * Get the "id" composantDocument.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<ComposantDocumentDTO> findOne(String id);

  /**
   * Delete the "id" composantDocument.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the composantDocument corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<ComposantDocumentDTO> search(String query, Pageable pageable);
}
