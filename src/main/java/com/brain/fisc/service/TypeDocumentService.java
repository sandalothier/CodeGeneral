package com.brain.fisc.service;

import com.brain.fisc.service.dto.TypeDocumentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.brain.fisc.domain.TypeDocument}.
 */
public interface TypeDocumentService {
  /**
   * Save a typeDocument.
   *
   * @param typeDocumentDTO the entity to save.
   * @return the persisted entity.
   */
  TypeDocumentDTO save(TypeDocumentDTO typeDocumentDTO);

  /**
   * Updates a typeDocument.
   *
   * @param typeDocumentDTO the entity to update.
   * @return the persisted entity.
   */
  TypeDocumentDTO update(TypeDocumentDTO typeDocumentDTO);

  /**
   * Partially updates a typeDocument.
   *
   * @param typeDocumentDTO the entity to update partially.
   * @return the persisted entity.
   */
  Optional<TypeDocumentDTO> partialUpdate(TypeDocumentDTO typeDocumentDTO);

  /**
   * Get all the typeDocuments.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TypeDocumentDTO> findAll(Pageable pageable);

  /**
   * Get the "id" typeDocument.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  Optional<TypeDocumentDTO> findOne(String id);

  /**
   * Delete the "id" typeDocument.
   *
   * @param id the id of the entity.
   */
  void delete(String id);

  /**
   * Search for the typeDocument corresponding to the query.
   *
   * @param query the query of the search.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  Page<TypeDocumentDTO> search(String query, Pageable pageable);
}
