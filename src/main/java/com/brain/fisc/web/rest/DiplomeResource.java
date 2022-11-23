package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.DiplomeRepository;
import com.brain.fisc.service.DiplomeService;
import com.brain.fisc.service.dto.DiplomeDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Diplome}.
 */
@RestController
@RequestMapping("/api")
public class DiplomeResource {

  private final Logger log = LoggerFactory.getLogger(DiplomeResource.class);

  private static final String ENTITY_NAME = "codeGeneralDiplome";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final DiplomeService diplomeService;

  private final DiplomeRepository diplomeRepository;

  public DiplomeResource(DiplomeService diplomeService, DiplomeRepository diplomeRepository) {
    this.diplomeService = diplomeService;
    this.diplomeRepository = diplomeRepository;
  }

  /**
   * {@code POST  /diplomes} : Create a new diplome.
   *
   * @param diplomeDTO the diplomeDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diplomeDTO, or with status {@code 400 (Bad Request)} if the diplome has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/diplomes")
  public ResponseEntity<DiplomeDTO> createDiplome(@Valid @RequestBody DiplomeDTO diplomeDTO) throws URISyntaxException {
    log.debug("REST request to save Diplome : {}", diplomeDTO);
    if (diplomeDTO.getId() != null) {
      throw new BadRequestAlertException("A new diplome cannot already have an ID", ENTITY_NAME, "idexists");
    }
    DiplomeDTO result = diplomeService.save(diplomeDTO);
    return ResponseEntity
      .created(new URI("/api/diplomes/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /diplomes/:id} : Updates an existing diplome.
   *
   * @param id the id of the diplomeDTO to save.
   * @param diplomeDTO the diplomeDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diplomeDTO,
   * or with status {@code 400 (Bad Request)} if the diplomeDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the diplomeDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/diplomes/{id}")
  public ResponseEntity<DiplomeDTO> updateDiplome(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody DiplomeDTO diplomeDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Diplome : {}, {}", id, diplomeDTO);
    if (diplomeDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, diplomeDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!diplomeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    DiplomeDTO result = diplomeService.update(diplomeDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, diplomeDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /diplomes/:id} : Partial updates given fields of an existing diplome, field will ignore if it is null
   *
   * @param id the id of the diplomeDTO to save.
   * @param diplomeDTO the diplomeDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diplomeDTO,
   * or with status {@code 400 (Bad Request)} if the diplomeDTO is not valid,
   * or with status {@code 404 (Not Found)} if the diplomeDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the diplomeDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/diplomes/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<DiplomeDTO> partialUpdateDiplome(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody DiplomeDTO diplomeDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Diplome partially : {}, {}", id, diplomeDTO);
    if (diplomeDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, diplomeDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!diplomeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<DiplomeDTO> result = diplomeService.partialUpdate(diplomeDTO);

    return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, diplomeDTO.getId()));
  }

  /**
   * {@code GET  /diplomes} : get all the diplomes.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diplomes in body.
   */
  @GetMapping("/diplomes")
  public ResponseEntity<List<DiplomeDTO>> getAllDiplomes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Diplomes");
    Page<DiplomeDTO> page = diplomeService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /diplomes/:id} : get the "id" diplome.
   *
   * @param id the id of the diplomeDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diplomeDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/diplomes/{id}")
  public ResponseEntity<DiplomeDTO> getDiplome(@PathVariable String id) {
    log.debug("REST request to get Diplome : {}", id);
    Optional<DiplomeDTO> diplomeDTO = diplomeService.findOne(id);
    return ResponseUtil.wrapOrNotFound(diplomeDTO);
  }

  /**
   * {@code DELETE  /diplomes/:id} : delete the "id" diplome.
   *
   * @param id the id of the diplomeDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/diplomes/{id}")
  public ResponseEntity<Void> deleteDiplome(@PathVariable String id) {
    log.debug("REST request to delete Diplome : {}", id);
    diplomeService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/diplomes?query=:query} : search for the diplome corresponding
   * to the query.
   *
   * @param query the query of the diplome search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/diplomes")
  public ResponseEntity<List<DiplomeDTO>> searchDiplomes(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Diplomes for query {}", query);
    Page<DiplomeDTO> page = diplomeService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
