package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.CongeRepository;
import com.brain.fisc.service.CongeService;
import com.brain.fisc.service.dto.CongeDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Conge}.
 */
@RestController
@RequestMapping("/api")
public class CongeResource {

  private final Logger log = LoggerFactory.getLogger(CongeResource.class);

  private static final String ENTITY_NAME = "codeGeneralConge";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final CongeService congeService;

  private final CongeRepository congeRepository;

  public CongeResource(CongeService congeService, CongeRepository congeRepository) {
    this.congeService = congeService;
    this.congeRepository = congeRepository;
  }

  /**
   * {@code POST  /conges} : Create a new conge.
   *
   * @param congeDTO the congeDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new congeDTO, or with status {@code 400 (Bad Request)} if the conge has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/conges")
  public ResponseEntity<CongeDTO> createConge(@Valid @RequestBody CongeDTO congeDTO) throws URISyntaxException {
    log.debug("REST request to save Conge : {}", congeDTO);
    if (congeDTO.getId() != null) {
      throw new BadRequestAlertException("A new conge cannot already have an ID", ENTITY_NAME, "idexists");
    }
    CongeDTO result = congeService.save(congeDTO);
    return ResponseEntity
      .created(new URI("/api/conges/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /conges/:id} : Updates an existing conge.
   *
   * @param id the id of the congeDTO to save.
   * @param congeDTO the congeDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated congeDTO,
   * or with status {@code 400 (Bad Request)} if the congeDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the congeDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/conges/{id}")
  public ResponseEntity<CongeDTO> updateConge(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody CongeDTO congeDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Conge : {}, {}", id, congeDTO);
    if (congeDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, congeDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!congeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    CongeDTO result = congeService.update(congeDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, congeDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /conges/:id} : Partial updates given fields of an existing conge, field will ignore if it is null
   *
   * @param id the id of the congeDTO to save.
   * @param congeDTO the congeDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated congeDTO,
   * or with status {@code 400 (Bad Request)} if the congeDTO is not valid,
   * or with status {@code 404 (Not Found)} if the congeDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the congeDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/conges/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<CongeDTO> partialUpdateConge(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody CongeDTO congeDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Conge partially : {}, {}", id, congeDTO);
    if (congeDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, congeDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!congeRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<CongeDTO> result = congeService.partialUpdate(congeDTO);

    return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, congeDTO.getId()));
  }

  /**
   * {@code GET  /conges} : get all the conges.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conges in body.
   */
  @GetMapping("/conges")
  public ResponseEntity<List<CongeDTO>> getAllConges(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Conges");
    Page<CongeDTO> page = congeService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /conges/:id} : get the "id" conge.
   *
   * @param id the id of the congeDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the congeDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/conges/{id}")
  public ResponseEntity<CongeDTO> getConge(@PathVariable String id) {
    log.debug("REST request to get Conge : {}", id);
    Optional<CongeDTO> congeDTO = congeService.findOne(id);
    return ResponseUtil.wrapOrNotFound(congeDTO);
  }

  /**
   * {@code DELETE  /conges/:id} : delete the "id" conge.
   *
   * @param id the id of the congeDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/conges/{id}")
  public ResponseEntity<Void> deleteConge(@PathVariable String id) {
    log.debug("REST request to delete Conge : {}", id);
    congeService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/conges?query=:query} : search for the conge corresponding
   * to the query.
   *
   * @param query the query of the conge search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/conges")
  public ResponseEntity<List<CongeDTO>> searchConges(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Conges for query {}", query);
    Page<CongeDTO> page = congeService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
