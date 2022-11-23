package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.ComposantDocumentRepository;
import com.brain.fisc.service.ComposantDocumentService;
import com.brain.fisc.service.dto.ComposantDocumentDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.ComposantDocument}.
 */
@RestController
@RequestMapping("/api")
public class ComposantDocumentResource {

  private final Logger log = LoggerFactory.getLogger(ComposantDocumentResource.class);

  private static final String ENTITY_NAME = "codeGeneralComposantDocument";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final ComposantDocumentService composantDocumentService;

  private final ComposantDocumentRepository composantDocumentRepository;

  public ComposantDocumentResource(
    ComposantDocumentService composantDocumentService,
    ComposantDocumentRepository composantDocumentRepository
  ) {
    this.composantDocumentService = composantDocumentService;
    this.composantDocumentRepository = composantDocumentRepository;
  }

  /**
   * {@code POST  /composant-documents} : Create a new composantDocument.
   *
   * @param composantDocumentDTO the composantDocumentDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new composantDocumentDTO, or with status {@code 400 (Bad Request)} if the composantDocument has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/composant-documents")
  public ResponseEntity<ComposantDocumentDTO> createComposantDocument(@Valid @RequestBody ComposantDocumentDTO composantDocumentDTO)
    throws URISyntaxException {
    log.debug("REST request to save ComposantDocument : {}", composantDocumentDTO);
    if (composantDocumentDTO.getId() != null) {
      throw new BadRequestAlertException("A new composantDocument cannot already have an ID", ENTITY_NAME, "idexists");
    }
    ComposantDocumentDTO result = composantDocumentService.save(composantDocumentDTO);
    return ResponseEntity
      .created(new URI("/api/composant-documents/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /composant-documents/:id} : Updates an existing composantDocument.
   *
   * @param id the id of the composantDocumentDTO to save.
   * @param composantDocumentDTO the composantDocumentDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated composantDocumentDTO,
   * or with status {@code 400 (Bad Request)} if the composantDocumentDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the composantDocumentDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/composant-documents/{id}")
  public ResponseEntity<ComposantDocumentDTO> updateComposantDocument(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody ComposantDocumentDTO composantDocumentDTO
  ) throws URISyntaxException {
    log.debug("REST request to update ComposantDocument : {}, {}", id, composantDocumentDTO);
    if (composantDocumentDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, composantDocumentDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!composantDocumentRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    ComposantDocumentDTO result = composantDocumentService.update(composantDocumentDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, composantDocumentDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /composant-documents/:id} : Partial updates given fields of an existing composantDocument, field will ignore if it is null
   *
   * @param id the id of the composantDocumentDTO to save.
   * @param composantDocumentDTO the composantDocumentDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated composantDocumentDTO,
   * or with status {@code 400 (Bad Request)} if the composantDocumentDTO is not valid,
   * or with status {@code 404 (Not Found)} if the composantDocumentDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the composantDocumentDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/composant-documents/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<ComposantDocumentDTO> partialUpdateComposantDocument(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody ComposantDocumentDTO composantDocumentDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update ComposantDocument partially : {}, {}", id, composantDocumentDTO);
    if (composantDocumentDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, composantDocumentDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!composantDocumentRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<ComposantDocumentDTO> result = composantDocumentService.partialUpdate(composantDocumentDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, composantDocumentDTO.getId())
    );
  }

  /**
   * {@code GET  /composant-documents} : get all the composantDocuments.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of composantDocuments in body.
   */
  @GetMapping("/composant-documents")
  public ResponseEntity<List<ComposantDocumentDTO>> getAllComposantDocuments(
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to get a page of ComposantDocuments");
    Page<ComposantDocumentDTO> page = composantDocumentService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /composant-documents/:id} : get the "id" composantDocument.
   *
   * @param id the id of the composantDocumentDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the composantDocumentDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/composant-documents/{id}")
  public ResponseEntity<ComposantDocumentDTO> getComposantDocument(@PathVariable String id) {
    log.debug("REST request to get ComposantDocument : {}", id);
    Optional<ComposantDocumentDTO> composantDocumentDTO = composantDocumentService.findOne(id);
    return ResponseUtil.wrapOrNotFound(composantDocumentDTO);
  }

  /**
   * {@code DELETE  /composant-documents/:id} : delete the "id" composantDocument.
   *
   * @param id the id of the composantDocumentDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/composant-documents/{id}")
  public ResponseEntity<Void> deleteComposantDocument(@PathVariable String id) {
    log.debug("REST request to delete ComposantDocument : {}", id);
    composantDocumentService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/composant-documents?query=:query} : search for the composantDocument corresponding
   * to the query.
   *
   * @param query the query of the composantDocument search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/composant-documents")
  public ResponseEntity<List<ComposantDocumentDTO>> searchComposantDocuments(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of ComposantDocuments for query {}", query);
    Page<ComposantDocumentDTO> page = composantDocumentService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
