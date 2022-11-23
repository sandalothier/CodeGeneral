package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.PersonnelRepository;
import com.brain.fisc.service.PersonnelService;
import com.brain.fisc.service.dto.PersonnelDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Personnel}.
 */
@RestController
@RequestMapping("/api")
public class PersonnelResource {

  private final Logger log = LoggerFactory.getLogger(PersonnelResource.class);

  private static final String ENTITY_NAME = "codeGeneralPersonnel";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final PersonnelService personnelService;

  private final PersonnelRepository personnelRepository;

  public PersonnelResource(PersonnelService personnelService, PersonnelRepository personnelRepository) {
    this.personnelService = personnelService;
    this.personnelRepository = personnelRepository;
  }

  /**
   * {@code POST  /personnel} : Create a new personnel.
   *
   * @param personnelDTO the personnelDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personnelDTO, or with status {@code 400 (Bad Request)} if the personnel has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/personnel")
  public ResponseEntity<PersonnelDTO> createPersonnel(@Valid @RequestBody PersonnelDTO personnelDTO) throws URISyntaxException {
    log.debug("REST request to save Personnel : {}", personnelDTO);
    if (personnelDTO.getId() != null) {
      throw new BadRequestAlertException("A new personnel cannot already have an ID", ENTITY_NAME, "idexists");
    }
    PersonnelDTO result = personnelService.save(personnelDTO);
    return ResponseEntity
      .created(new URI("/api/personnel/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /personnel/:id} : Updates an existing personnel.
   *
   * @param id the id of the personnelDTO to save.
   * @param personnelDTO the personnelDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personnelDTO,
   * or with status {@code 400 (Bad Request)} if the personnelDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the personnelDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/personnel/{id}")
  public ResponseEntity<PersonnelDTO> updatePersonnel(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody PersonnelDTO personnelDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Personnel : {}, {}", id, personnelDTO);
    if (personnelDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, personnelDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!personnelRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    PersonnelDTO result = personnelService.update(personnelDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, personnelDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /personnel/:id} : Partial updates given fields of an existing personnel, field will ignore if it is null
   *
   * @param id the id of the personnelDTO to save.
   * @param personnelDTO the personnelDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personnelDTO,
   * or with status {@code 400 (Bad Request)} if the personnelDTO is not valid,
   * or with status {@code 404 (Not Found)} if the personnelDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the personnelDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/personnel/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<PersonnelDTO> partialUpdatePersonnel(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody PersonnelDTO personnelDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Personnel partially : {}, {}", id, personnelDTO);
    if (personnelDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, personnelDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!personnelRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<PersonnelDTO> result = personnelService.partialUpdate(personnelDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, personnelDTO.getId())
    );
  }

  /**
   * {@code GET  /personnel} : get all the personnel.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personnel in body.
   */
  @GetMapping("/personnel")
  public ResponseEntity<List<PersonnelDTO>> getAllPersonnel(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Personnel");
    Page<PersonnelDTO> page = personnelService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /personnel/:id} : get the "id" personnel.
   *
   * @param id the id of the personnelDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personnelDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/personnel/{id}")
  public ResponseEntity<PersonnelDTO> getPersonnel(@PathVariable String id) {
    log.debug("REST request to get Personnel : {}", id);
    Optional<PersonnelDTO> personnelDTO = personnelService.findOne(id);
    return ResponseUtil.wrapOrNotFound(personnelDTO);
  }

  /**
   * {@code DELETE  /personnel/:id} : delete the "id" personnel.
   *
   * @param id the id of the personnelDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/personnel/{id}")
  public ResponseEntity<Void> deletePersonnel(@PathVariable String id) {
    log.debug("REST request to delete Personnel : {}", id);
    personnelService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/personnel?query=:query} : search for the personnel corresponding
   * to the query.
   *
   * @param query the query of the personnel search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/personnel")
  public ResponseEntity<List<PersonnelDTO>> searchPersonnel(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Personnel for query {}", query);
    Page<PersonnelDTO> page = personnelService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
