package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.AdresseRepository;
import com.brain.fisc.service.AdresseService;
import com.brain.fisc.service.dto.AdresseDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Adresse}.
 */
@RestController
@RequestMapping("/api")
public class AdresseResource {

  private final Logger log = LoggerFactory.getLogger(AdresseResource.class);

  private static final String ENTITY_NAME = "codeGeneralAdresse";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final AdresseService adresseService;

  private final AdresseRepository adresseRepository;

  public AdresseResource(AdresseService adresseService, AdresseRepository adresseRepository) {
    this.adresseService = adresseService;
    this.adresseRepository = adresseRepository;
  }

  /**
   * {@code POST  /adresses} : Create a new adresse.
   *
   * @param adresseDTO the adresseDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new adresseDTO, or with status {@code 400 (Bad Request)} if the adresse has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/adresses")
  public ResponseEntity<AdresseDTO> createAdresse(@Valid @RequestBody AdresseDTO adresseDTO) throws URISyntaxException {
    log.debug("REST request to save Adresse : {}", adresseDTO);
    if (adresseDTO.getId() != null) {
      throw new BadRequestAlertException("A new adresse cannot already have an ID", ENTITY_NAME, "idexists");
    }
    AdresseDTO result = adresseService.save(adresseDTO);
    return ResponseEntity
      .created(new URI("/api/adresses/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /adresses/:id} : Updates an existing adresse.
   *
   * @param id the id of the adresseDTO to save.
   * @param adresseDTO the adresseDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adresseDTO,
   * or with status {@code 400 (Bad Request)} if the adresseDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the adresseDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/adresses/{id}")
  public ResponseEntity<AdresseDTO> updateAdresse(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody AdresseDTO adresseDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Adresse : {}, {}", id, adresseDTO);
    if (adresseDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, adresseDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!adresseRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    AdresseDTO result = adresseService.update(adresseDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, adresseDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /adresses/:id} : Partial updates given fields of an existing adresse, field will ignore if it is null
   *
   * @param id the id of the adresseDTO to save.
   * @param adresseDTO the adresseDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adresseDTO,
   * or with status {@code 400 (Bad Request)} if the adresseDTO is not valid,
   * or with status {@code 404 (Not Found)} if the adresseDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the adresseDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/adresses/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<AdresseDTO> partialUpdateAdresse(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody AdresseDTO adresseDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Adresse partially : {}, {}", id, adresseDTO);
    if (adresseDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, adresseDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!adresseRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<AdresseDTO> result = adresseService.partialUpdate(adresseDTO);

    return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, adresseDTO.getId()));
  }

  /**
   * {@code GET  /adresses} : get all the adresses.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of adresses in body.
   */
  @GetMapping("/adresses")
  public ResponseEntity<List<AdresseDTO>> getAllAdresses(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Adresses");
    Page<AdresseDTO> page = adresseService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /adresses/:id} : get the "id" adresse.
   *
   * @param id the id of the adresseDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the adresseDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/adresses/{id}")
  public ResponseEntity<AdresseDTO> getAdresse(@PathVariable String id) {
    log.debug("REST request to get Adresse : {}", id);
    Optional<AdresseDTO> adresseDTO = adresseService.findOne(id);
    return ResponseUtil.wrapOrNotFound(adresseDTO);
  }

  /**
   * {@code DELETE  /adresses/:id} : delete the "id" adresse.
   *
   * @param id the id of the adresseDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/adresses/{id}")
  public ResponseEntity<Void> deleteAdresse(@PathVariable String id) {
    log.debug("REST request to delete Adresse : {}", id);
    adresseService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/adresses?query=:query} : search for the adresse corresponding
   * to the query.
   *
   * @param query the query of the adresse search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/adresses")
  public ResponseEntity<List<AdresseDTO>> searchAdresses(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Adresses for query {}", query);
    Page<AdresseDTO> page = adresseService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
