package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.TypeContratDeTravailRepository;
import com.brain.fisc.service.TypeContratDeTravailService;
import com.brain.fisc.service.dto.TypeContratDeTravailDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.TypeContratDeTravail}.
 */
@RestController
@RequestMapping("/api")
public class TypeContratDeTravailResource {

  private final Logger log = LoggerFactory.getLogger(TypeContratDeTravailResource.class);

  private static final String ENTITY_NAME = "codeGeneralTypeContratDeTravail";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final TypeContratDeTravailService typeContratDeTravailService;

  private final TypeContratDeTravailRepository typeContratDeTravailRepository;

  public TypeContratDeTravailResource(
    TypeContratDeTravailService typeContratDeTravailService,
    TypeContratDeTravailRepository typeContratDeTravailRepository
  ) {
    this.typeContratDeTravailService = typeContratDeTravailService;
    this.typeContratDeTravailRepository = typeContratDeTravailRepository;
  }

  /**
   * {@code POST  /type-contrat-de-travails} : Create a new typeContratDeTravail.
   *
   * @param typeContratDeTravailDTO the typeContratDeTravailDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeContratDeTravailDTO, or with status {@code 400 (Bad Request)} if the typeContratDeTravail has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/type-contrat-de-travails")
  public ResponseEntity<TypeContratDeTravailDTO> createTypeContratDeTravail(
    @Valid @RequestBody TypeContratDeTravailDTO typeContratDeTravailDTO
  ) throws URISyntaxException {
    log.debug("REST request to save TypeContratDeTravail : {}", typeContratDeTravailDTO);
    if (typeContratDeTravailDTO.getId() != null) {
      throw new BadRequestAlertException("A new typeContratDeTravail cannot already have an ID", ENTITY_NAME, "idexists");
    }
    TypeContratDeTravailDTO result = typeContratDeTravailService.save(typeContratDeTravailDTO);
    return ResponseEntity
      .created(new URI("/api/type-contrat-de-travails/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /type-contrat-de-travails/:id} : Updates an existing typeContratDeTravail.
   *
   * @param id the id of the typeContratDeTravailDTO to save.
   * @param typeContratDeTravailDTO the typeContratDeTravailDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDeTravailDTO,
   * or with status {@code 400 (Bad Request)} if the typeContratDeTravailDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the typeContratDeTravailDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/type-contrat-de-travails/{id}")
  public ResponseEntity<TypeContratDeTravailDTO> updateTypeContratDeTravail(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody TypeContratDeTravailDTO typeContratDeTravailDTO
  ) throws URISyntaxException {
    log.debug("REST request to update TypeContratDeTravail : {}, {}", id, typeContratDeTravailDTO);
    if (typeContratDeTravailDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, typeContratDeTravailDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!typeContratDeTravailRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    TypeContratDeTravailDTO result = typeContratDeTravailService.update(typeContratDeTravailDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, typeContratDeTravailDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /type-contrat-de-travails/:id} : Partial updates given fields of an existing typeContratDeTravail, field will ignore if it is null
   *
   * @param id the id of the typeContratDeTravailDTO to save.
   * @param typeContratDeTravailDTO the typeContratDeTravailDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDeTravailDTO,
   * or with status {@code 400 (Bad Request)} if the typeContratDeTravailDTO is not valid,
   * or with status {@code 404 (Not Found)} if the typeContratDeTravailDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the typeContratDeTravailDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/type-contrat-de-travails/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<TypeContratDeTravailDTO> partialUpdateTypeContratDeTravail(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody TypeContratDeTravailDTO typeContratDeTravailDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update TypeContratDeTravail partially : {}, {}", id, typeContratDeTravailDTO);
    if (typeContratDeTravailDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, typeContratDeTravailDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!typeContratDeTravailRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<TypeContratDeTravailDTO> result = typeContratDeTravailService.partialUpdate(typeContratDeTravailDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, typeContratDeTravailDTO.getId())
    );
  }

  /**
   * {@code GET  /type-contrat-de-travails} : get all the typeContratDeTravails.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeContratDeTravails in body.
   */
  @GetMapping("/type-contrat-de-travails")
  public ResponseEntity<List<TypeContratDeTravailDTO>> getAllTypeContratDeTravails(
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to get a page of TypeContratDeTravails");
    Page<TypeContratDeTravailDTO> page = typeContratDeTravailService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /type-contrat-de-travails/:id} : get the "id" typeContratDeTravail.
   *
   * @param id the id of the typeContratDeTravailDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeContratDeTravailDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/type-contrat-de-travails/{id}")
  public ResponseEntity<TypeContratDeTravailDTO> getTypeContratDeTravail(@PathVariable String id) {
    log.debug("REST request to get TypeContratDeTravail : {}", id);
    Optional<TypeContratDeTravailDTO> typeContratDeTravailDTO = typeContratDeTravailService.findOne(id);
    return ResponseUtil.wrapOrNotFound(typeContratDeTravailDTO);
  }

  /**
   * {@code DELETE  /type-contrat-de-travails/:id} : delete the "id" typeContratDeTravail.
   *
   * @param id the id of the typeContratDeTravailDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/type-contrat-de-travails/{id}")
  public ResponseEntity<Void> deleteTypeContratDeTravail(@PathVariable String id) {
    log.debug("REST request to delete TypeContratDeTravail : {}", id);
    typeContratDeTravailService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/type-contrat-de-travails?query=:query} : search for the typeContratDeTravail corresponding
   * to the query.
   *
   * @param query the query of the typeContratDeTravail search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/type-contrat-de-travails")
  public ResponseEntity<List<TypeContratDeTravailDTO>> searchTypeContratDeTravails(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of TypeContratDeTravails for query {}", query);
    Page<TypeContratDeTravailDTO> page = typeContratDeTravailService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
