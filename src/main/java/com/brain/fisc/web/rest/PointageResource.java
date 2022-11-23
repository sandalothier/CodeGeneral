package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.PointageRepository;
import com.brain.fisc.service.PointageService;
import com.brain.fisc.service.dto.PointageDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Pointage}.
 */
@RestController
@RequestMapping("/api")
public class PointageResource {

  private final Logger log = LoggerFactory.getLogger(PointageResource.class);

  private static final String ENTITY_NAME = "codeGeneralPointage";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final PointageService pointageService;

  private final PointageRepository pointageRepository;

  public PointageResource(PointageService pointageService, PointageRepository pointageRepository) {
    this.pointageService = pointageService;
    this.pointageRepository = pointageRepository;
  }

  /**
   * {@code POST  /pointages} : Create a new pointage.
   *
   * @param pointageDTO the pointageDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pointageDTO, or with status {@code 400 (Bad Request)} if the pointage has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/pointages")
  public ResponseEntity<PointageDTO> createPointage(@Valid @RequestBody PointageDTO pointageDTO) throws URISyntaxException {
    log.debug("REST request to save Pointage : {}", pointageDTO);
    if (pointageDTO.getId() != null) {
      throw new BadRequestAlertException("A new pointage cannot already have an ID", ENTITY_NAME, "idexists");
    }
    PointageDTO result = pointageService.save(pointageDTO);
    return ResponseEntity
      .created(new URI("/api/pointages/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /pointages/:id} : Updates an existing pointage.
   *
   * @param id the id of the pointageDTO to save.
   * @param pointageDTO the pointageDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pointageDTO,
   * or with status {@code 400 (Bad Request)} if the pointageDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the pointageDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/pointages/{id}")
  public ResponseEntity<PointageDTO> updatePointage(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody PointageDTO pointageDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Pointage : {}, {}", id, pointageDTO);
    if (pointageDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, pointageDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!pointageRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    PointageDTO result = pointageService.update(pointageDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pointageDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /pointages/:id} : Partial updates given fields of an existing pointage, field will ignore if it is null
   *
   * @param id the id of the pointageDTO to save.
   * @param pointageDTO the pointageDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pointageDTO,
   * or with status {@code 400 (Bad Request)} if the pointageDTO is not valid,
   * or with status {@code 404 (Not Found)} if the pointageDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the pointageDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/pointages/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<PointageDTO> partialUpdatePointage(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody PointageDTO pointageDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Pointage partially : {}, {}", id, pointageDTO);
    if (pointageDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, pointageDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!pointageRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<PointageDTO> result = pointageService.partialUpdate(pointageDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pointageDTO.getId())
    );
  }

  /**
   * {@code GET  /pointages} : get all the pointages.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pointages in body.
   */
  @GetMapping("/pointages")
  public ResponseEntity<List<PointageDTO>> getAllPointages(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Pointages");
    Page<PointageDTO> page = pointageService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /pointages/:id} : get the "id" pointage.
   *
   * @param id the id of the pointageDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pointageDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/pointages/{id}")
  public ResponseEntity<PointageDTO> getPointage(@PathVariable String id) {
    log.debug("REST request to get Pointage : {}", id);
    Optional<PointageDTO> pointageDTO = pointageService.findOne(id);
    return ResponseUtil.wrapOrNotFound(pointageDTO);
  }

  /**
   * {@code DELETE  /pointages/:id} : delete the "id" pointage.
   *
   * @param id the id of the pointageDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/pointages/{id}")
  public ResponseEntity<Void> deletePointage(@PathVariable String id) {
    log.debug("REST request to delete Pointage : {}", id);
    pointageService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/pointages?query=:query} : search for the pointage corresponding
   * to the query.
   *
   * @param query the query of the pointage search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/pointages")
  public ResponseEntity<List<PointageDTO>> searchPointages(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Pointages for query {}", query);
    Page<PointageDTO> page = pointageService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
