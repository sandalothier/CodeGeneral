package com.brain.fisc.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.repository.PermissionRepository;
import com.brain.fisc.service.PermissionService;
import com.brain.fisc.service.dto.PermissionDTO;
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
 * REST controller for managing {@link com.brain.fisc.domain.Permission}.
 */
@RestController
@RequestMapping("/api")
public class PermissionResource {

  private final Logger log = LoggerFactory.getLogger(PermissionResource.class);

  private static final String ENTITY_NAME = "codeGeneralPermission";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final PermissionService permissionService;

  private final PermissionRepository permissionRepository;

  public PermissionResource(PermissionService permissionService, PermissionRepository permissionRepository) {
    this.permissionService = permissionService;
    this.permissionRepository = permissionRepository;
  }

  /**
   * {@code POST  /permissions} : Create a new permission.
   *
   * @param permissionDTO the permissionDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new permissionDTO, or with status {@code 400 (Bad Request)} if the permission has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/permissions")
  public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) throws URISyntaxException {
    log.debug("REST request to save Permission : {}", permissionDTO);
    if (permissionDTO.getId() != null) {
      throw new BadRequestAlertException("A new permission cannot already have an ID", ENTITY_NAME, "idexists");
    }
    PermissionDTO result = permissionService.save(permissionDTO);
    return ResponseEntity
      .created(new URI("/api/permissions/" + result.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
      .body(result);
  }

  /**
   * {@code PUT  /permissions/:id} : Updates an existing permission.
   *
   * @param id the id of the permissionDTO to save.
   * @param permissionDTO the permissionDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionDTO,
   * or with status {@code 400 (Bad Request)} if the permissionDTO is not valid,
   * or with status {@code 500 (Internal Server Error)} if the permissionDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/permissions/{id}")
  public ResponseEntity<PermissionDTO> updatePermission(
    @PathVariable(value = "id", required = false) final String id,
    @Valid @RequestBody PermissionDTO permissionDTO
  ) throws URISyntaxException {
    log.debug("REST request to update Permission : {}, {}", id, permissionDTO);
    if (permissionDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, permissionDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!permissionRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    PermissionDTO result = permissionService.update(permissionDTO);
    return ResponseEntity
      .ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, permissionDTO.getId()))
      .body(result);
  }

  /**
   * {@code PATCH  /permissions/:id} : Partial updates given fields of an existing permission, field will ignore if it is null
   *
   * @param id the id of the permissionDTO to save.
   * @param permissionDTO the permissionDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionDTO,
   * or with status {@code 400 (Bad Request)} if the permissionDTO is not valid,
   * or with status {@code 404 (Not Found)} if the permissionDTO is not found,
   * or with status {@code 500 (Internal Server Error)} if the permissionDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/permissions/{id}", consumes = { "application/json", "application/merge-patch+json" })
  public ResponseEntity<PermissionDTO> partialUpdatePermission(
    @PathVariable(value = "id", required = false) final String id,
    @NotNull @RequestBody PermissionDTO permissionDTO
  ) throws URISyntaxException {
    log.debug("REST request to partial update Permission partially : {}, {}", id, permissionDTO);
    if (permissionDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, permissionDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!permissionRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    Optional<PermissionDTO> result = permissionService.partialUpdate(permissionDTO);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, permissionDTO.getId())
    );
  }

  /**
   * {@code GET  /permissions} : get all the permissions.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of permissions in body.
   */
  @GetMapping("/permissions")
  public ResponseEntity<List<PermissionDTO>> getAllPermissions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    log.debug("REST request to get a page of Permissions");
    Page<PermissionDTO> page = permissionService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /permissions/:id} : get the "id" permission.
   *
   * @param id the id of the permissionDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the permissionDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/permissions/{id}")
  public ResponseEntity<PermissionDTO> getPermission(@PathVariable String id) {
    log.debug("REST request to get Permission : {}", id);
    Optional<PermissionDTO> permissionDTO = permissionService.findOne(id);
    return ResponseUtil.wrapOrNotFound(permissionDTO);
  }

  /**
   * {@code DELETE  /permissions/:id} : delete the "id" permission.
   *
   * @param id the id of the permissionDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/permissions/{id}")
  public ResponseEntity<Void> deletePermission(@PathVariable String id) {
    log.debug("REST request to delete Permission : {}", id);
    permissionService.delete(id);
    return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
  }

  /**
   * {@code SEARCH  /_search/permissions?query=:query} : search for the permission corresponding
   * to the query.
   *
   * @param query the query of the permission search.
   * @param pageable the pagination information.
   * @return the result of the search.
   */
  @GetMapping("/_search/permissions")
  public ResponseEntity<List<PermissionDTO>> searchPermissions(
    @RequestParam String query,
    @org.springdoc.api.annotations.ParameterObject Pageable pageable
  ) {
    log.debug("REST request to search for a page of Permissions for query {}", query);
    Page<PermissionDTO> page = permissionService.search(query, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }
}
