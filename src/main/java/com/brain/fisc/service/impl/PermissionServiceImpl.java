package com.brain.fisc.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.brain.fisc.domain.Permission;
import com.brain.fisc.repository.PermissionRepository;
import com.brain.fisc.repository.search.PermissionSearchRepository;
import com.brain.fisc.service.PermissionService;
import com.brain.fisc.service.dto.PermissionDTO;
import com.brain.fisc.service.mapper.PermissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Permission}.
 */
@Service
public class PermissionServiceImpl implements PermissionService {

  private final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

  private final PermissionRepository permissionRepository;

  private final PermissionMapper permissionMapper;

  private final PermissionSearchRepository permissionSearchRepository;

  public PermissionServiceImpl(
    PermissionRepository permissionRepository,
    PermissionMapper permissionMapper,
    PermissionSearchRepository permissionSearchRepository
  ) {
    this.permissionRepository = permissionRepository;
    this.permissionMapper = permissionMapper;
    this.permissionSearchRepository = permissionSearchRepository;
  }

  @Override
  public PermissionDTO save(PermissionDTO permissionDTO) {
    log.debug("Request to save Permission : {}", permissionDTO);
    Permission permission = permissionMapper.toEntity(permissionDTO);
    permission = permissionRepository.save(permission);
    PermissionDTO result = permissionMapper.toDto(permission);
    permissionSearchRepository.index(permission);
    return result;
  }

  @Override
  public PermissionDTO update(PermissionDTO permissionDTO) {
    log.debug("Request to update Permission : {}", permissionDTO);
    Permission permission = permissionMapper.toEntity(permissionDTO);
    permission = permissionRepository.save(permission);
    PermissionDTO result = permissionMapper.toDto(permission);
    permissionSearchRepository.index(permission);
    return result;
  }

  @Override
  public Optional<PermissionDTO> partialUpdate(PermissionDTO permissionDTO) {
    log.debug("Request to partially update Permission : {}", permissionDTO);

    return permissionRepository
      .findById(permissionDTO.getId())
      .map(existingPermission -> {
        permissionMapper.partialUpdate(existingPermission, permissionDTO);

        return existingPermission;
      })
      .map(permissionRepository::save)
      .map(savedPermission -> {
        permissionSearchRepository.save(savedPermission);

        return savedPermission;
      })
      .map(permissionMapper::toDto);
  }

  @Override
  public Page<PermissionDTO> findAll(Pageable pageable) {
    log.debug("Request to get all Permissions");
    return permissionRepository.findAll(pageable).map(permissionMapper::toDto);
  }

  @Override
  public Optional<PermissionDTO> findOne(String id) {
    log.debug("Request to get Permission : {}", id);
    return permissionRepository.findById(id).map(permissionMapper::toDto);
  }

  @Override
  public void delete(String id) {
    log.debug("Request to delete Permission : {}", id);
    permissionRepository.deleteById(id);
    permissionSearchRepository.deleteById(id);
  }

  @Override
  public Page<PermissionDTO> search(String query, Pageable pageable) {
    log.debug("Request to search for a page of Permissions for query {}", query);
    return permissionSearchRepository.search(query, pageable).map(permissionMapper::toDto);
  }
}
