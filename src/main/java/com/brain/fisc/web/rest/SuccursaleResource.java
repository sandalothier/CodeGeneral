package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.SuccursaleRepository;
import com.brain.fisc.service.SuccursaleService;
import com.brain.fisc.service.dto.SuccursaleDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Succursale}.
 */
@RestController
@RequestMapping("/api")
public class SuccursaleResource {

  private final Logger log = LoggerFactory.getLogger(SuccursaleResource.class);

  private static final String ENTITY_NAME = "codeGeneralSuccursale";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final SuccursaleService succursaleService;

  private final SuccursaleRepository succursaleRepository;

  public SuccursaleResource(SuccursaleService succursaleService, SuccursaleRepository succursaleRepository) {
    this.succursaleService = succursaleService;
    this.succursaleRepository = succursaleRepository;
  }

  /**
   * {@code POST  /succursales} : Create a new succursale.
   *
   * @param succursaleDTO the succursaleDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new succursaleDTO, or with status {@code 400 (Bad Request)} if the succursale has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/succursales")
  public ResponseEntity<SuccursaleDTO> createSuccursale(@Valid @RequestBody SuccursaleDTO succursaleDTO) throws URISyntaxException {
    log.debug("REST request to save Succursale : {}", succursaleDTO);
    if (succursaleDTO.getId() != null) {
      throw new BadRequestAlertException("A new succursale cannot already have an ID", ENTITY_NAME, "idexists");
    }
    SuccursaleDTO result = succursaleService.save(succursaleDTO);
    return ResponseEntity
      .created(new URI("/api/succursales/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /succursales/:id} : Updates an existing succursale.
   *
   * @param id the id of the succursaleDTO to save.
   * @param succursaleDTO the succursaleDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated succursaleDTO,
   * or with status {@code 400 (Bad Request)} if the succursaleDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the succursaleDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/succursales/{id}")
  public ResponseEntity<SuccursaleDTO> updateSuccursale(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody SuccursaleDTO succursaleDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Succursale : {}, {}", id, succursaleDTO);
    if (succursaleDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, succursaleDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!succursaleRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    SuccursaleDTO result = succursaleService.update(succursaleDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, succursaleDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /succursales/:id} : Partial updates given fields of an existing succursale, field will ignore if it is null
   *
   * @param id the id of the succursaleDTO to save.
   * @param succursaleDTO the succursaleDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated succursaleDTO,
   * or with status {@code 400 (Bad Request)} if the succursaleDTO is not valid,
   * or with status {@code 404 (Not Found)} if the succursaleDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the succursaleDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/succursales/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<SuccursaleDTO> partialUpdateSuccursale(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody SuccursaleDTO succursaleDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Succursale partially : {}, {}", id, succursaleDTO);
    if (succursaleDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, succursaleDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!succursaleRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<SuccursaleDTO> result = succursaleService.partialUpdate(succursaleDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, succursaleDTO.getId())
    );
  }

  /**
   * {@code GET  /succursales} : get all the succursales.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of succursales in body.
   */
  @GetMapping("/succursales")
  public ResponseEntity<List<SuccursaleDTO>> getAllSuccursales(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Succursales");
    Page<SuccursaleDTO> page = succursaleService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /succursales/:id} : get the "id" succursale.
   *
   * @param id the id of the succursaleDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the succursaleDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/succursales/{id}")
  public ResponseEntity<SuccursaleDTO> getSuccursale(@PathVariable String id) {
    log.debug("REST request to get Succursale : {}", id);
    Optional<SuccursaleDTO> succursaleDTO = succursaleService.findOne(id);
    return ResponseUtil.wrapOrNotFound(succursaleDTO);
  }

  /**
   * {@code DELETE  /succursales/:id} : delete the "id" succursale.
   *
   * @param id the id of the succursaleDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/succursales/{id}")
  public ResponseEntity<Void> deleteSuccursale(@PathVariable String id) {
    log.debug("REST request to delete Succursale : {}", id);
    succursaleService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/succursales?query=:query} : search for the succursale corresponding
   * to the query.
   *
   * @param query the query of the succursale search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/succursales")
  public ResponseEntity<List<SuccursaleDTO>> searchSuccursales(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Succursales for query {}", query);
    Page<SuccursaleDTO> page = succursaleService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
