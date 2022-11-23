package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.DateOperationRepository;
import com.brain.fisc.service.DateOperationService;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link com.brain.fisc.domain.DateOperation}.
 */
@RestController
@RequestMapping("/api")
public class DateOperationResource {

  private final Logger log = LoggerFactory.getLogger(DateOperationResource.class);

  private static final String ENTITY_NAME = "codeGeneralDateOperation";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final DateOperationService dateOperationService;

  private final DateOperationRepository dateOperationRepository;

  public DateOperationResource(DateOperationService dateOperationService, DateOperationRepository dateOperationRepository) {
    this.dateOperationService = dateOperationService;
    this.dateOperationRepository = dateOperationRepository;
  }

  /**
   * {@code POST  /date-operations} : Create a new dateOperation.
   *
   * @param dateOperationDTO the dateOperationDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dateOperationDTO, or with status {@code 400 (Bad Request)} if the dateOperation has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/date-operations")
  public ResponseEntity<DateOperationDTO> createDateOperation(@RequestBody DateOperationDTO dateOperationDTO) throws URISyntaxException {
    log.debug("REST request to save DateOperation : {}", dateOperationDTO);
    if (dateOperationDTO.getId() != null) {
      throw new BadRequestAlertException("A new dateOperation cannot already have an ID", ENTITY_NAME, "idexists");
    }
    DateOperationDTO result = dateOperationService.save(dateOperationDTO);
    return ResponseEntity
      .created(new URI("/api/date-operations/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /date-operations/:id} : Updates an existing dateOperation.
   *
   * @param id the id of the dateOperationDTO to save.
   * @param dateOperationDTO the dateOperationDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dateOperationDTO,
   * or with status {@code 400 (Bad Request)} if the dateOperationDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the dateOperationDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/date-operations/{id}")
  public ResponseEntity<DateOperationDTO> updateDateOperation(
    @PathVariable(value = "id", required = false) final String id,
    @RequestBody DateOperationDTO dateOperationDTO
  ) throws URISyntaxException {
    log.debug("REST request to update DateOperation : {}, {}", id, dateOperationDTO);
    if (dateOperationDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, dateOperationDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!dateOperationRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    DateOperationDTO result = dateOperationService.update(dateOperationDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dateOperationDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /date-operations/:id} : Partial updates given fields of an existing dateOperation, field will ignore if it is null
   *
   * @param id the id of the dateOperationDTO to save.
   * @param dateOperationDTO the dateOperationDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dateOperationDTO,
   * or with status {@code 400 (Bad Request)} if the dateOperationDTO is not valid,
   * or with status {@code 404 (Not Found)} if the dateOperationDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the dateOperationDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/date-operations/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<DateOperationDTO> partialUpdateDateOperation(
    @PathVariable(value = "id", required = false) final String id,
    @RequestBody DateOperationDTO dateOperationDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update DateOperation partially : {}, {}", id, dateOperationDTO);
    if (dateOperationDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, dateOperationDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!dateOperationRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<DateOperationDTO> result = dateOperationService.partialUpdate(dateOperationDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dateOperationDTO.getId())
    );
  }

  /**
   * {@code GET  /date-operations} : get all the dateOperations.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dateOperations in body.
   */
  @GetMapping("/date-operations")
  public ResponseEntity<List<DateOperationDTO>> getAllDateOperations(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of DateOperations");
    Page<DateOperationDTO> page = dateOperationService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /date-operations/:id} : get the "id" dateOperation.
   *
   * @param id the id of the dateOperationDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dateOperationDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/date-operations/{id}")
  public ResponseEntity<DateOperationDTO> getDateOperation(@PathVariable String id) {
    log.debug("REST request to get DateOperation : {}", id);
    Optional<DateOperationDTO> dateOperationDTO = dateOperationService.findOne(id);
    return ResponseUtil.wrapOrNotFound(dateOperationDTO);
  }

  /**
   * {@code DELETE  /date-operations/:id} : delete the "id" dateOperation.
   *
   * @param id the id of the dateOperationDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/date-operations/{id}")
  public ResponseEntity<Void> deleteDateOperation(@PathVariable String id) {
    log.debug("REST request to delete DateOperation : {}", id);
    dateOperationService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/date-operations?query=:query} : search for the dateOperation corresponding
   * to the query.
   *
   * @param query the query of the dateOperation search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/date-operations")
  public ResponseEntity<List<DateOperationDTO>> searchDateOperations(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of DateOperations for query {}", query);
    Page<DateOperationDTO> page = dateOperationService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
