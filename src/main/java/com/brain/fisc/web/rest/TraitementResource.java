package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.TraitementRepository;
import com.brain.fisc.service.TraitementService;
import com.brain.fisc.service.dto.TraitementDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Traitement}.
 */
@RestController
@RequestMapping("/api")
public class TraitementResource {

  private final Logger log = LoggerFactory.getLogger(TraitementResource.class);

  private static final String ENTITY_NAME = "codeGeneralTraitement";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final TraitementService traitementService;

  private final TraitementRepository traitementRepository;

  public TraitementResource(TraitementService traitementService, TraitementRepository traitementRepository) {
    this.traitementService = traitementService;
    this.traitementRepository = traitementRepository;
  }

  /**
   * {@code POST  /traitements} : Create a new traitement.
   *
   * @param traitementDTO the traitementDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new traitementDTO, or with status {@code 400 (Bad Request)} if the traitement has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/traitements")
  public ResponseEntity<TraitementDTO> createTraitement(@Valid @RequestBody TraitementDTO traitementDTO) throws URISyntaxException {
    log.debug("REST request to save Traitement : {}", traitementDTO);
    if (traitementDTO.getId() != null) {
      throw new BadRequestAlertException("A new traitement cannot already have an ID", ENTITY_NAME, "idexists");
    }
    TraitementDTO result = traitementService.save(traitementDTO);
    return ResponseEntity
      .created(new URI("/api/traitements/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /traitements/:id} : Updates an existing traitement.
   *
   * @param id the id of the traitementDTO to save.
   * @param traitementDTO the traitementDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traitementDTO,
   * or with status {@code 400 (Bad Request)} if the traitementDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the traitementDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/traitements/{id}")
  public ResponseEntity<TraitementDTO> updateTraitement(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody TraitementDTO traitementDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Traitement : {}, {}", id, traitementDTO);
    if (traitementDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, traitementDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!traitementRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    TraitementDTO result = traitementService.update(traitementDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, traitementDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /traitements/:id} : Partial updates given fields of an existing traitement, field will ignore if it is null
   *
   * @param id the id of the traitementDTO to save.
   * @param traitementDTO the traitementDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traitementDTO,
   * or with status {@code 400 (Bad Request)} if the traitementDTO is not valid,
   * or with status {@code 404 (Not Found)} if the traitementDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the traitementDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/traitements/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<TraitementDTO> partialUpdateTraitement(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody TraitementDTO traitementDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Traitement partially : {}, {}", id, traitementDTO);
    if (traitementDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, traitementDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!traitementRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<TraitementDTO> result = traitementService.partialUpdate(traitementDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, traitementDTO.getId())
    );
  }

  /**
   * {@code GET  /traitements} : get all the traitements.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of traitements in body.
   */
  @GetMapping("/traitements")
  public ResponseEntity<List<TraitementDTO>> getAllTraitements(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Traitements");
    Page<TraitementDTO> page = traitementService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /traitements/:id} : get the "id" traitement.
   *
   * @param id the id of the traitementDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the traitementDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/traitements/{id}")
  public ResponseEntity<TraitementDTO> getTraitement(@PathVariable String id) {
    log.debug("REST request to get Traitement : {}", id);
    Optional<TraitementDTO> traitementDTO = traitementService.findOne(id);
    return ResponseUtil.wrapOrNotFound(traitementDTO);
  }

  /**
   * {@code DELETE  /traitements/:id} : delete the "id" traitement.
   *
   * @param id the id of the traitementDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/traitements/{id}")
  public ResponseEntity<Void> deleteTraitement(@PathVariable String id) {
    log.debug("REST request to delete Traitement : {}", id);
    traitementService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/traitements?query=:query} : search for the traitement corresponding
   * to the query.
   *
   * @param query the query of the traitement search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/traitements")
  public ResponseEntity<List<TraitementDTO>> searchTraitements(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Traitements for query {}", query);
    Page<TraitementDTO> page = traitementService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
