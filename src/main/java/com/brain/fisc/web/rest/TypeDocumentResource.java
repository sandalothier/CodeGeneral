package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.TypeDocumentRepository;
import com.brain.fisc.service.TypeDocumentService;
import com.brain.fisc.service.dto.TypeDocumentDTO;
import com.brain.fisc.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.brain.fisc.domain.TypeDocument}.
 */
@RestController
@RequestMapping("/api")
public class TypeDocumentResource {

  private final Logger log = LoggerFactory.getLogger(TypeDocumentResource.class);

  private static final String ENTITY_NAME = "codeGeneralTypeDocument";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final TypeDocumentService typeDocumentService;

  private final TypeDocumentRepository typeDocumentRepository;

  public TypeDocumentResource(TypeDocumentService typeDocumentService, TypeDocumentRepository typeDocumentRepository) {
    this.typeDocumentService = typeDocumentService;
    this.typeDocumentRepository = typeDocumentRepository;
  }

  /**
   * {@code POST  /type-documents} : Create a new typeDocument.
   *
   * @param typeDocumentDTO the typeDocumentDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeDocumentDTO, or with status {@code 400 (Bad Request)} if the typeDocument has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/type-documents")
  public ResponseEntity<TypeDocumentDTO> createTypeDocument(@Valid @RequestBody TypeDocumentDTO typeDocumentDTO) throws URISyntaxException {
    log.debug("REST request to save TypeDocument : {}", typeDocumentDTO);
    if (typeDocumentDTO.getId() != null) {
      throw new BadRequestAlertException("A new typeDocument cannot already have an ID", ENTITY_NAME, "idexists");
    }
    TypeDocumentDTO result = typeDocumentService.save(typeDocumentDTO);
    return ResponseEntity
      .created(new URI("/api/type-documents/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /type-documents/:id} : Updates an existing typeDocument.
   *
   * @param id the id of the typeDocumentDTO to save.
   * @param typeDocumentDTO the typeDocumentDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeDocumentDTO,
   * or with status {@code 400 (Bad Request)} if the typeDocumentDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the typeDocumentDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/type-documents/{id}")
  public ResponseEntity<TypeDocumentDTO> updateTypeDocument(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody TypeDocumentDTO typeDocumentDTO
  ) throws URISyntaxException {
    log.debug("REST request to update TypeDocument : {}, {}", id, typeDocumentDTO);
    if (typeDocumentDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, typeDocumentDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!typeDocumentRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    TypeDocumentDTO result = typeDocumentService.update(typeDocumentDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, typeDocumentDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /type-documents/:id} : Partial updates given fields of an existing typeDocument, field will ignore if it is null
   *
   * @param id the id of the typeDocumentDTO to save.
   * @param typeDocumentDTO the typeDocumentDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeDocumentDTO,
   * or with status {@code 400 (Bad Request)} if the typeDocumentDTO is not valid,
   * or with status {@code 404 (Not Found)} if the typeDocumentDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the typeDocumentDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/type-documents/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<TypeDocumentDTO> partialUpdateTypeDocument(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody TypeDocumentDTO typeDocumentDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update TypeDocument partially : {}, {}", id, typeDocumentDTO);
    if (typeDocumentDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, typeDocumentDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!typeDocumentRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<TypeDocumentDTO> result = typeDocumentService.partialUpdate(typeDocumentDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, typeDocumentDTO.getId())
    );
  }

  /**
   * {@code GET  /type-documents} : get all the typeDocuments.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeDocuments in body.
   */
  @GetMapping("/type-documents")
  public ResponseEntity<List<TypeDocumentDTO>> getAllTypeDocuments(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of TypeDocuments");
    Page<TypeDocumentDTO> page = typeDocumentService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /type-documents/:id} : get the "id" typeDocument.
   *
   * @param id the id of the typeDocumentDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeDocumentDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/type-documents/{id}")
  public ResponseEntity<TypeDocumentDTO> getTypeDocument(@PathVariable String id) {
    log.debug("REST request to get TypeDocument : {}", id);
    Optional<TypeDocumentDTO> typeDocumentDTO = typeDocumentService.findOne(id);
    return ResponseUtil.wrapOrNotFound(typeDocumentDTO);
  }

  /**
   * {@code DELETE  /type-documents/:id} : delete the "id" typeDocument.
   *
   * @param id the id of the typeDocumentDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/type-documents/{id}")
  public ResponseEntity<Void> deleteTypeDocument(@PathVariable String id) {
    log.debug("REST request to delete TypeDocument : {}", id);
    typeDocumentService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/type-documents?query=:query} : search for the typeDocument corresponding
   * to the query.
   *
   * @param query the query of the typeDocument search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/type-documents")
  public ResponseEntity<List<TypeDocumentDTO>> searchTypeDocuments(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of TypeDocuments for query {}", query);
    Page<TypeDocumentDTO> page = typeDocumentService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
