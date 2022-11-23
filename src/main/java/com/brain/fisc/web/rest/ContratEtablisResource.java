package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.ContratEtablisRepository;
import com.brain.fisc.service.ContratEtablisService;
import com.brain.fisc.service.dto.ContratEtablisDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.ContratEtablis}.
 */
@RestController
@RequestMapping("/api")
public class ContratEtablisResource {

  private final Logger log = LoggerFactory.getLogger(ContratEtablisResource.class);

  private static final String ENTITY_NAME = "codeGeneralContratEtablis";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final ContratEtablisService contratEtablisService;

  private final ContratEtablisRepository contratEtablisRepository;

  public ContratEtablisResource(ContratEtablisService contratEtablisService, ContratEtablisRepository contratEtablisRepository) {
    this.contratEtablisService = contratEtablisService;
    this.contratEtablisRepository = contratEtablisRepository;
  }

  /**
   * {@code POST  /contrat-etablis} : Create a new contratEtablis.
   *
   * @param contratEtablisDTO the contratEtablisDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contratEtablisDTO, or with status {@code 400 (Bad Request)} if the contratEtablis has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/contrat-etablis")
  public ResponseEntity<ContratEtablisDTO> createContratEtablis(@Valid @RequestBody ContratEtablisDTO contratEtablisDTO)
    throws URISyntaxException {
    log.debug("REST request to save ContratEtablis : {}", contratEtablisDTO);
    if (contratEtablisDTO.getId() != null) {
      throw new BadRequestAlertException("A new contratEtablis cannot already have an ID", ENTITY_NAME, "idexists");
    }
    ContratEtablisDTO result = contratEtablisService.save(contratEtablisDTO);
    return ResponseEntity
      .created(new URI("/api/contrat-etablis/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /contrat-etablis/:id} : Updates an existing contratEtablis.
   *
   * @param id the id of the contratEtablisDTO to save.
   * @param contratEtablisDTO the contratEtablisDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratEtablisDTO,
   * or with status {@code 400 (Bad Request)} if the contratEtablisDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the contratEtablisDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/contrat-etablis/{id}")
  public ResponseEntity<ContratEtablisDTO> updateContratEtablis(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody ContratEtablisDTO contratEtablisDTO
  ) throws URISyntaxException {
    log.debug("REST request to update ContratEtablis : {}, {}", id, contratEtablisDTO);
    if (contratEtablisDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, contratEtablisDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!contratEtablisRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    ContratEtablisDTO result = contratEtablisService.update(contratEtablisDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contratEtablisDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /contrat-etablis/:id} : Partial updates given fields of an existing contratEtablis, field will ignore if it is null
   *
   * @param id the id of the contratEtablisDTO to save.
   * @param contratEtablisDTO the contratEtablisDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratEtablisDTO,
   * or with status {@code 400 (Bad Request)} if the contratEtablisDTO is not valid,
   * or with status {@code 404 (Not Found)} if the contratEtablisDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the contratEtablisDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/contrat-etablis/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<ContratEtablisDTO> partialUpdateContratEtablis(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody ContratEtablisDTO contratEtablisDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update ContratEtablis partially : {}, {}", id, contratEtablisDTO);
    if (contratEtablisDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, contratEtablisDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!contratEtablisRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<ContratEtablisDTO> result = contratEtablisService.partialUpdate(contratEtablisDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contratEtablisDTO.getId())
    );
  }

  /**
   * {@code GET  /contrat-etablis} : get all the contratEtablis.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contratEtablis in body.
   */
  @GetMapping("/contrat-etablis")
  public ResponseEntity<List<ContratEtablisDTO>> getAllContratEtablis(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of ContratEtablis");
    Page<ContratEtablisDTO> page = contratEtablisService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /contrat-etablis/:id} : get the "id" contratEtablis.
   *
   * @param id the id of the contratEtablisDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contratEtablisDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/contrat-etablis/{id}")
  public ResponseEntity<ContratEtablisDTO> getContratEtablis(@PathVariable String id) {
    log.debug("REST request to get ContratEtablis : {}", id);
    Optional<ContratEtablisDTO> contratEtablisDTO = contratEtablisService.findOne(id);
    return ResponseUtil.wrapOrNotFound(contratEtablisDTO);
  }

  /**
   * {@code DELETE  /contrat-etablis/:id} : delete the "id" contratEtablis.
   *
   * @param id the id of the contratEtablisDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/contrat-etablis/{id}")
  public ResponseEntity<Void> deleteContratEtablis(@PathVariable String id) {
    log.debug("REST request to delete ContratEtablis : {}", id);
    contratEtablisService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/contrat-etablis?query=:query} : search for the contratEtablis corresponding
   * to the query.
   *
   * @param query the query of the contratEtablis search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/contrat-etablis")
  public ResponseEntity<List<ContratEtablisDTO>> searchContratEtablis(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of ContratEtablis for query {}", query);
    Page<ContratEtablisDTO> page = contratEtablisService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
