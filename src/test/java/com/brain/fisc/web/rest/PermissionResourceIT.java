package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Permission;
import com.brain.fisc.repository.PermissionRepository;
import com.brain.fisc.repository.search.PermissionSearchRepository;
import com.brain.fisc.service.dto.PermissionDTO;
import com.brain.fisc.service.mapper.PermissionMapper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link PermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PermissionResourceIT {

  private static final String DEFAULT_REFPERMISSION = "AAAAAAAAAA";
  private static final String UPDATED_REFPERMISSION = "BBBBBBBBBB";

  private static final String DEFAULT_INT_PERMISSION = "AAAAAAAAAA";
  private static final String UPDATED_INT_PERMISSION = "BBBBBBBBBB";

  private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
  private static final String UPDATED_MOTIF = "BBBBBBBBBB";

  private static final Integer DEFAULT_DEDUCT_PERM = 1;
  private static final Integer UPDATED_DEDUCT_PERM = 2;

  private static final String ENTITY_API_URL = "/api/permissions";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/permissions";

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private PermissionMapper permissionMapper;

  @Autowired
  private PermissionSearchRepository permissionSearchRepository;

  @Autowired
  private MockMvc restPermissionMockMvc;

  private Permission permission;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Permission createEntity() {
    Permission permission = new Permission()
      .refpermission(DEFAULT_REFPERMISSION)
      .intPermission(DEFAULT_INT_PERMISSION)
      .motif(DEFAULT_MOTIF)
      .deductPerm(DEFAULT_DEDUCT_PERM);
    return permission;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Permission createUpdatedEntity() {
    Permission permission = new Permission()
      .refpermission(UPDATED_REFPERMISSION)
      .intPermission(UPDATED_INT_PERMISSION)
      .motif(UPDATED_MOTIF)
      .deductPerm(UPDATED_DEDUCT_PERM);
    return permission;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    permissionSearchRepository.deleteAll();
    assertThat(permissionSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    permissionRepository.deleteAll();
    permission = createEntity();
  }

  @Test
  void createPermission() throws Exception {
    int databaseSizeBeforeCreate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);
    restPermissionMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isCreated());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Permission testPermission = permissionList.get(permissionList.size() - 1);
    assertThat(testPermission.getRefpermission()).isEqualTo(DEFAULT_REFPERMISSION);
    assertThat(testPermission.getIntPermission()).isEqualTo(DEFAULT_INT_PERMISSION);
    assertThat(testPermission.getMotif()).isEqualTo(DEFAULT_MOTIF);
    assertThat(testPermission.getDeductPerm()).isEqualTo(DEFAULT_DEDUCT_PERM);
  }

  @Test
  void createPermissionWithExistingId() throws Exception {
    // Create the Permission with an existing ID
    permission.setId("existing_id");
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    int databaseSizeBeforeCreate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restPermissionMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkRefpermissionIsRequired() throws Exception {
    int databaseSizeBeforeTest = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    // set the field null
    permission.setRefpermission(null);

    // Create the Permission, which fails.
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    restPermissionMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isBadRequest());

    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkMotifIsRequired() throws Exception {
    int databaseSizeBeforeTest = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    // set the field null
    permission.setMotif(null);

    // Create the Permission, which fails.
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    restPermissionMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isBadRequest());

    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllPermissions() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);

    // Get all the permissionList
    restPermissionMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(permission.getId())))
      .andExpect(jsonPath("$.[*].refpermission").value(hasItem(DEFAULT_REFPERMISSION)))
      .andExpect(jsonPath("$.[*].intPermission").value(hasItem(DEFAULT_INT_PERMISSION)))
      .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
      .andExpect(jsonPath("$.[*].deductPerm").value(hasItem(DEFAULT_DEDUCT_PERM)));
  }

  @Test
  void getPermission() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);

    // Get the permission
    restPermissionMockMvc
      .perform(get(ENTITY_API_URL_ID, permission.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(permission.getId()))
      .andExpect(jsonPath("$.refpermission").value(DEFAULT_REFPERMISSION))
      .andExpect(jsonPath("$.intPermission").value(DEFAULT_INT_PERMISSION))
      .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
      .andExpect(jsonPath("$.deductPerm").value(DEFAULT_DEDUCT_PERM));
  }

  @Test
  void getNonExistingPermission() throws Exception {
    // Get the permission
    restPermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingPermission() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);

    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    permissionSearchRepository.save(permission);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());

    // Update the permission
    Permission updatedPermission = permissionRepository.findById(permission.getId()).get();
    updatedPermission
      .refpermission(UPDATED_REFPERMISSION)
      .intPermission(UPDATED_INT_PERMISSION)
      .motif(UPDATED_MOTIF)
      .deductPerm(UPDATED_DEDUCT_PERM);
    PermissionDTO permissionDTO = permissionMapper.toDto(updatedPermission);

    restPermissionMockMvc
      .perform(
        put(ENTITY_API_URL_ID, permissionDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(permissionDTO))
      )
      .andExpect(status().isOk());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    Permission testPermission = permissionList.get(permissionList.size() - 1);
    assertThat(testPermission.getRefpermission()).isEqualTo(UPDATED_REFPERMISSION);
    assertThat(testPermission.getIntPermission()).isEqualTo(UPDATED_INT_PERMISSION);
    assertThat(testPermission.getMotif()).isEqualTo(UPDATED_MOTIF);
    assertThat(testPermission.getDeductPerm()).isEqualTo(UPDATED_DEDUCT_PERM);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Permission> permissionSearchList = IterableUtils.toList(permissionSearchRepository.findAll());
        Permission testPermissionSearch = permissionSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testPermissionSearch.getRefpermission()).isEqualTo(UPDATED_REFPERMISSION);
        assertThat(testPermissionSearch.getIntPermission()).isEqualTo(UPDATED_INT_PERMISSION);
        assertThat(testPermissionSearch.getMotif()).isEqualTo(UPDATED_MOTIF);
        assertThat(testPermissionSearch.getDeductPerm()).isEqualTo(UPDATED_DEDUCT_PERM);
      });
  }

  @Test
  void putNonExistingPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(
        put(ENTITY_API_URL_ID, permissionDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(permissionDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(permissionDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdatePermissionWithPatch() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);

    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();

    // Update the permission using partial update
    Permission partialUpdatedPermission = new Permission();
    partialUpdatedPermission.setId(permission.getId());

    partialUpdatedPermission
      .refpermission(UPDATED_REFPERMISSION)
      .intPermission(UPDATED_INT_PERMISSION)
      .motif(UPDATED_MOTIF)
      .deductPerm(UPDATED_DEDUCT_PERM);

    restPermissionMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPermission))
      )
      .andExpect(status().isOk());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    Permission testPermission = permissionList.get(permissionList.size() - 1);
    assertThat(testPermission.getRefpermission()).isEqualTo(UPDATED_REFPERMISSION);
    assertThat(testPermission.getIntPermission()).isEqualTo(UPDATED_INT_PERMISSION);
    assertThat(testPermission.getMotif()).isEqualTo(UPDATED_MOTIF);
    assertThat(testPermission.getDeductPerm()).isEqualTo(UPDATED_DEDUCT_PERM);
  }

  @Test
  void fullUpdatePermissionWithPatch() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);

    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();

    // Update the permission using partial update
    Permission partialUpdatedPermission = new Permission();
    partialUpdatedPermission.setId(permission.getId());

    partialUpdatedPermission
      .refpermission(UPDATED_REFPERMISSION)
      .intPermission(UPDATED_INT_PERMISSION)
      .motif(UPDATED_MOTIF)
      .deductPerm(UPDATED_DEDUCT_PERM);

    restPermissionMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPermission))
      )
      .andExpect(status().isOk());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    Permission testPermission = permissionList.get(permissionList.size() - 1);
    assertThat(testPermission.getRefpermission()).isEqualTo(UPDATED_REFPERMISSION);
    assertThat(testPermission.getIntPermission()).isEqualTo(UPDATED_INT_PERMISSION);
    assertThat(testPermission.getMotif()).isEqualTo(UPDATED_MOTIF);
    assertThat(testPermission.getDeductPerm()).isEqualTo(UPDATED_DEDUCT_PERM);
  }

  @Test
  void patchNonExistingPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, permissionDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(permissionDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(permissionDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamPermission() throws Exception {
    int databaseSizeBeforeUpdate = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    permission.setId(UUID.randomUUID().toString());

    // Create the Permission
    PermissionDTO permissionDTO = permissionMapper.toDto(permission);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPermissionMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(permissionDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Permission in the database
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deletePermission() throws Exception {
    // Initialize the database
    permissionRepository.save(permission);
    permissionRepository.save(permission);
    permissionSearchRepository.save(permission);

    int databaseSizeBeforeDelete = permissionRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the permission
    restPermissionMockMvc
      .perform(delete(ENTITY_API_URL_ID, permission.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Permission> permissionList = permissionRepository.findAll();
    assertThat(permissionList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(permissionSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchPermission() throws Exception {
    // Initialize the database
    permission = permissionRepository.save(permission);
    permissionSearchRepository.save(permission);

    // Search the permission
    restPermissionMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + permission.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(permission.getId())))
      .andExpect(jsonPath("$.[*].refpermission").value(hasItem(DEFAULT_REFPERMISSION)))
      .andExpect(jsonPath("$.[*].intPermission").value(hasItem(DEFAULT_INT_PERMISSION)))
      .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
      .andExpect(jsonPath("$.[*].deductPerm").value(hasItem(DEFAULT_DEDUCT_PERM)));
  }
}
