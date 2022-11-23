package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.BulletinPaieRepository;
import com.brain.fisc.service.BulletinPaieService;
import com.brain.fisc.service.dto.BulletinPaieDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.BulletinPaie}.
 */
@RestController
@RequestMapping("/api")
public class BulletinPaieResource {

  private final Logger log = LoggerFactory.getLogger(BulletinPaieResource.class);

  private static final String ENTITY_NAME = "codeGeneralBulletinPaie";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final BulletinPaieService bulletinPaieService;

  private final BulletinPaieRepository bulletinPaieRepository;

  public BulletinPaieResource(BulletinPaieService bulletinPaieService, BulletinPaieRepository bulletinPaieRepository) {
    this.bulletinPaieService = bulletinPaieService;
    this.bulletinPaieRepository = bulletinPaieRepository;
  }

  /**
   * {@code POST  /bulletin-paies} : Create a new bulletinPaie.
   *
   * @param bulletinPaieDTO the bulletinPaieDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bulletinPaieDTO, or with status {@code 400 (Bad Request)} if the bulletinPaie has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/bulletin-paies")
  public ResponseEntity<BulletinPaieDTO> createBulletinPaie(@Valid @RequestBody BulletinPaieDTO bulletinPaieDTO) throws URISyntaxException {
    log.debug("REST request to save BulletinPaie : {}", bulletinPaieDTO);
    if (bulletinPaieDTO.getId() != null) {
      throw new BadRequestAlertException("A new bulletinPaie cannot already have an ID", ENTITY_NAME, "idexists");
    }
    BulletinPaieDTO result = bulletinPaieService.save(bulletinPaieDTO);
    return ResponseEntity
      .created(new URI("/api/bulletin-paies/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /bulletin-paies/:id} : Updates an existing bulletinPaie.
   *
   * @param id the id of the bulletinPaieDTO to save.
   * @param bulletinPaieDTO the bulletinPaieDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bulletinPaieDTO,
   * or with status {@code 400 (Bad Request)} if the bulletinPaieDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the bulletinPaieDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/bulletin-paies/{id}")
  public ResponseEntity<BulletinPaieDTO> updateBulletinPaie(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody BulletinPaieDTO bulletinPaieDTO
  ) throws URISyntaxException {
    log.debug("REST request to update BulletinPaie : {}, {}", id, bulletinPaieDTO);
    if (bulletinPaieDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, bulletinPaieDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!bulletinPaieRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    BulletinPaieDTO result = bulletinPaieService.update(bulletinPaieDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bulletinPaieDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /bulletin-paies/:id} : Partial updates given fields of an existing bulletinPaie, field will ignore if it is null
   *
   * @param id the id of the bulletinPaieDTO to save.
   * @param bulletinPaieDTO the bulletinPaieDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bulletinPaieDTO,
   * or with status {@code 400 (Bad Request)} if the bulletinPaieDTO is not valid,
   * or with status {@code 404 (Not Found)} if the bulletinPaieDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the bulletinPaieDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/bulletin-paies/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<BulletinPaieDTO> partialUpdateBulletinPaie(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody BulletinPaieDTO bulletinPaieDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update BulletinPaie partially : {}, {}", id, bulletinPaieDTO);
    if (bulletinPaieDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, bulletinPaieDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!bulletinPaieRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<BulletinPaieDTO> result = bulletinPaieService.partialUpdate(bulletinPaieDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bulletinPaieDTO.getId())
    );
  }

  /**
   * {@code GET  /bulletin-paies} : get all the bulletinPaies.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bulletinPaies in body.
   */
  @GetMapping("/bulletin-paies")
  public ResponseEntity<List<BulletinPaieDTO>> getAllBulletinPaies(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of BulletinPaies");
    Page<BulletinPaieDTO> page = bulletinPaieService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /bulletin-paies/:id} : get the "id" bulletinPaie.
   *
   * @param id the id of the bulletinPaieDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bulletinPaieDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/bulletin-paies/{id}")
  public ResponseEntity<BulletinPaieDTO> getBulletinPaie(@PathVariable String id) {
    log.debug("REST request to get BulletinPaie : {}", id);
    Optional<BulletinPaieDTO> bulletinPaieDTO = bulletinPaieService.findOne(id);
    return ResponseUtil.wrapOrNotFound(bulletinPaieDTO);
  }

  /**
   * {@code DELETE  /bulletin-paies/:id} : delete the "id" bulletinPaie.
   *
   * @param id the id of the bulletinPaieDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/bulletin-paies/{id}")
  public ResponseEntity<Void> deleteBulletinPaie(@PathVariable String id) {
    log.debug("REST request to delete BulletinPaie : {}", id);
    bulletinPaieService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/bulletin-paies?query=:query} : search for the bulletinPaie corresponding
   * to the query.
   *
   * @param query the query of the bulletinPaie search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/bulletin-paies")
  public ResponseEntity<List<BulletinPaieDTO>> searchBulletinPaies(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of BulletinPaies for query {}", query);
    Page<BulletinPaieDTO> page = bulletinPaieService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
