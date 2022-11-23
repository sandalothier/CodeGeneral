package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.TypeContratDeTravail;
import com.brain.fisc.repository.TypeContratDeTravailRepository;
import com.brain.fisc.repository.search.TypeContratDeTravailSearchRepository;
import com.brain.fisc.service.dto.TypeContratDeTravailDTO;
import com.brain.fisc.service.mapper.TypeContratDeTravailMapper;
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
 * Integration tests for the {@link TypeContratDeTravailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeContratDeTravailResourceIT {

  private static final String DEFAULT_INT_TYPE_CONTRAT = "AAAAAAAAAA";
  private static final String UPDATED_INT_TYPE_CONTRAT = "BBBBBBBBBB";

  private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
  private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

  private static final Integer DEFAULT_DUREE_MAX = 1;
  private static final Integer UPDATED_DUREE_MAX = 2;

  private static final String ENTITY_API_URL = "/api/type-contrat-de-travails";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/type-contrat-de-travails";

  @Autowired
  private TypeContratDeTravailRepository typeContratDeTravailRepository;

  @Autowired
  private TypeContratDeTravailMapper typeContratDeTravailMapper;

  @Autowired
  private TypeContratDeTravailSearchRepository typeContratDeTravailSearchRepository;

  @Autowired
  private MockMvc restTypeContratDeTravailMockMvc;

  private TypeContratDeTravail typeContratDeTravail;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static TypeContratDeTravail createEntity() {
    TypeContratDeTravail typeContratDeTravail = new TypeContratDeTravail()
      .intTypeContrat(DEFAULT_INT_TYPE_CONTRAT)
      .description(DEFAULT_DESCRIPTION)
      .dureeMax(DEFAULT_DUREE_MAX);
    return typeContratDeTravail;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static TypeContratDeTravail createUpdatedEntity() {
    TypeContratDeTravail typeContratDeTravail = new TypeContratDeTravail()
      .intTypeContrat(UPDATED_INT_TYPE_CONTRAT)
      .description(UPDATED_DESCRIPTION)
      .dureeMax(UPDATED_DUREE_MAX);
    return typeContratDeTravail;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    typeContratDeTravailSearchRepository.deleteAll();
    assertThat(typeContratDeTravailSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    typeContratDeTravailRepository.deleteAll();
    typeContratDeTravail = createEntity();
  }

  @Test
  void createTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeCreate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);
    restTypeContratDeTravailMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isCreated());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    TypeContratDeTravail testTypeContratDeTravail = typeContratDeTravailList.get(typeContratDeTravailList.size() - 1);
    assertThat(testTypeContratDeTravail.getIntTypeContrat()).isEqualTo(DEFAULT_INT_TYPE_CONTRAT);
    assertThat(testTypeContratDeTravail.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    assertThat(testTypeContratDeTravail.getDureeMax()).isEqualTo(DEFAULT_DUREE_MAX);
  }

  @Test
  void createTypeContratDeTravailWithExistingId() throws Exception {
    // Create the TypeContratDeTravail with an existing ID
    typeContratDeTravail.setId("existing_id");
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    int databaseSizeBeforeCreate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restTypeContratDeTravailMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkDescriptionIsRequired() throws Exception {
    int databaseSizeBeforeTest = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    // set the field null
    typeContratDeTravail.setDescription(null);

    // Create the TypeContratDeTravail, which fails.
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    restTypeContratDeTravailMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkDureeMaxIsRequired() throws Exception {
    int databaseSizeBeforeTest = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    // set the field null
    typeContratDeTravail.setDureeMax(null);

    // Create the TypeContratDeTravail, which fails.
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    restTypeContratDeTravailMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllTypeContratDeTravails() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);

    // Get all the typeContratDeTravailList
    restTypeContratDeTravailMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(typeContratDeTravail.getId())))
      .andExpect(jsonPath("$.[*].intTypeContrat").value(hasItem(DEFAULT_INT_TYPE_CONTRAT)))
      .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
      .andExpect(jsonPath("$.[*].dureeMax").value(hasItem(DEFAULT_DUREE_MAX)));
  }

  @Test
  void getTypeContratDeTravail() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);

    // Get the typeContratDeTravail
    restTypeContratDeTravailMockMvc
      .perform(get(ENTITY_API_URL_ID, typeContratDeTravail.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(typeContratDeTravail.getId()))
      .andExpect(jsonPath("$.intTypeContrat").value(DEFAULT_INT_TYPE_CONTRAT))
      .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
      .andExpect(jsonPath("$.dureeMax").value(DEFAULT_DUREE_MAX));
  }

  @Test
  void getNonExistingTypeContratDeTravail() throws Exception {
    // Get the typeContratDeTravail
    restTypeContratDeTravailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingTypeContratDeTravail() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);

    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    typeContratDeTravailSearchRepository.save(typeContratDeTravail);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());

    // Update the typeContratDeTravail
    TypeContratDeTravail updatedTypeContratDeTravail = typeContratDeTravailRepository.findById(typeContratDeTravail.getId()).get();
    updatedTypeContratDeTravail.intTypeContrat(UPDATED_INT_TYPE_CONTRAT).description(UPDATED_DESCRIPTION).dureeMax(UPDATED_DUREE_MAX);
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(updatedTypeContratDeTravail);

    restTypeContratDeTravailMockMvc
      .perform(
        put(ENTITY_API_URL_ID, typeContratDeTravailDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isOk());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    TypeContratDeTravail testTypeContratDeTravail = typeContratDeTravailList.get(typeContratDeTravailList.size() - 1);
    assertThat(testTypeContratDeTravail.getIntTypeContrat()).isEqualTo(UPDATED_INT_TYPE_CONTRAT);
    assertThat(testTypeContratDeTravail.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testTypeContratDeTravail.getDureeMax()).isEqualTo(UPDATED_DUREE_MAX);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<TypeContratDeTravail> typeContratDeTravailSearchList = IterableUtils.toList(typeContratDeTravailSearchRepository.findAll());
        TypeContratDeTravail testTypeContratDeTravailSearch = typeContratDeTravailSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testTypeContratDeTravailSearch.getIntTypeContrat()).isEqualTo(UPDATED_INT_TYPE_CONTRAT);
        assertThat(testTypeContratDeTravailSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTypeContratDeTravailSearch.getDureeMax()).isEqualTo(UPDATED_DUREE_MAX);
      });
  }

  @Test
  void putNonExistingTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        put(ENTITY_API_URL_ID, typeContratDeTravailDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateTypeContratDeTravailWithPatch() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);

    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();

    // Update the typeContratDeTravail using partial update
    TypeContratDeTravail partialUpdatedTypeContratDeTravail = new TypeContratDeTravail();
    partialUpdatedTypeContratDeTravail.setId(typeContratDeTravail.getId());

    partialUpdatedTypeContratDeTravail.description(UPDATED_DESCRIPTION).dureeMax(UPDATED_DUREE_MAX);

    restTypeContratDeTravailMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTypeContratDeTravail.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeContratDeTravail))
      )
      .andExpect(status().isOk());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    TypeContratDeTravail testTypeContratDeTravail = typeContratDeTravailList.get(typeContratDeTravailList.size() - 1);
    assertThat(testTypeContratDeTravail.getIntTypeContrat()).isEqualTo(DEFAULT_INT_TYPE_CONTRAT);
    assertThat(testTypeContratDeTravail.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testTypeContratDeTravail.getDureeMax()).isEqualTo(UPDATED_DUREE_MAX);
  }

  @Test
  void fullUpdateTypeContratDeTravailWithPatch() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);

    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();

    // Update the typeContratDeTravail using partial update
    TypeContratDeTravail partialUpdatedTypeContratDeTravail = new TypeContratDeTravail();
    partialUpdatedTypeContratDeTravail.setId(typeContratDeTravail.getId());

    partialUpdatedTypeContratDeTravail
      .intTypeContrat(UPDATED_INT_TYPE_CONTRAT)
      .description(UPDATED_DESCRIPTION)
      .dureeMax(UPDATED_DUREE_MAX);

    restTypeContratDeTravailMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTypeContratDeTravail.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeContratDeTravail))
      )
      .andExpect(status().isOk());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    TypeContratDeTravail testTypeContratDeTravail = typeContratDeTravailList.get(typeContratDeTravailList.size() - 1);
    assertThat(testTypeContratDeTravail.getIntTypeContrat()).isEqualTo(UPDATED_INT_TYPE_CONTRAT);
    assertThat(testTypeContratDeTravail.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testTypeContratDeTravail.getDureeMax()).isEqualTo(UPDATED_DUREE_MAX);
  }

  @Test
  void patchNonExistingTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, typeContratDeTravailDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamTypeContratDeTravail() throws Exception {
    int databaseSizeBeforeUpdate = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    typeContratDeTravail.setId(UUID.randomUUID().toString());

    // Create the TypeContratDeTravail
    TypeContratDeTravailDTO typeContratDeTravailDTO = typeContratDeTravailMapper.toDto(typeContratDeTravail);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeContratDeTravailMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(typeContratDeTravailDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the TypeContratDeTravail in the database
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteTypeContratDeTravail() throws Exception {
    // Initialize the database
    typeContratDeTravailRepository.save(typeContratDeTravail);
    typeContratDeTravailRepository.save(typeContratDeTravail);
    typeContratDeTravailSearchRepository.save(typeContratDeTravail);

    int databaseSizeBeforeDelete = typeContratDeTravailRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the typeContratDeTravail
    restTypeContratDeTravailMockMvc
      .perform(delete(ENTITY_API_URL_ID, typeContratDeTravail.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<TypeContratDeTravail> typeContratDeTravailList = typeContratDeTravailRepository.findAll();
    assertThat(typeContratDeTravailList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeContratDeTravailSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchTypeContratDeTravail() throws Exception {
    // Initialize the database
    typeContratDeTravail = typeContratDeTravailRepository.save(typeContratDeTravail);
    typeContratDeTravailSearchRepository.save(typeContratDeTravail);

    // Search the typeContratDeTravail
    restTypeContratDeTravailMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + typeContratDeTravail.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(typeContratDeTravail.getId())))
      .andExpect(jsonPath("$.[*].intTypeContrat").value(hasItem(DEFAULT_INT_TYPE_CONTRAT)))
      .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
      .andExpect(jsonPath("$.[*].dureeMax").value(hasItem(DEFAULT_DUREE_MAX)));
  }
}
