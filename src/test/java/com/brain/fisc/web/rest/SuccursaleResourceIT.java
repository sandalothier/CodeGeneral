package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Succursale;
import com.brain.fisc.repository.SuccursaleRepository;
import com.brain.fisc.repository.search.SuccursaleSearchRepository;
import com.brain.fisc.service.dto.SuccursaleDTO;
import com.brain.fisc.service.mapper.SuccursaleMapper;
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
 * Integration tests for the {@link SuccursaleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SuccursaleResourceIT {

  private static final String DEFAULT_INT_SUCCURSALE = "AAAAAAAAAA";
  private static final String UPDATED_INT_SUCCURSALE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/succursales";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/succursales";

  @Autowired
  private SuccursaleRepository succursaleRepository;

  @Autowired
  private SuccursaleMapper succursaleMapper;

  @Autowired
  private SuccursaleSearchRepository succursaleSearchRepository;

  @Autowired
  private MockMvc restSuccursaleMockMvc;

  private Succursale succursale;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Succursale createEntity() {
    Succursale succursale = new Succursale().intSuccursale(DEFAULT_INT_SUCCURSALE);
    return succursale;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Succursale createUpdatedEntity() {
    Succursale succursale = new Succursale().intSuccursale(UPDATED_INT_SUCCURSALE);
    return succursale;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    succursaleSearchRepository.deleteAll();
    assertThat(succursaleSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    succursaleRepository.deleteAll();
    succursale = createEntity();
  }

  @Test
  void createSuccursale() throws Exception {
    int databaseSizeBeforeCreate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);
    restSuccursaleMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(succursaleDTO)))
      .andExpect(status().isCreated());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Succursale testSuccursale = succursaleList.get(succursaleList.size() - 1);
    assertThat(testSuccursale.getIntSuccursale()).isEqualTo(DEFAULT_INT_SUCCURSALE);
  }

  @Test
  void createSuccursaleWithExistingId() throws Exception {
    // Create the Succursale with an existing ID
    succursale.setId("existing_id");
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    int databaseSizeBeforeCreate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restSuccursaleMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(succursaleDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkIntSuccursaleIsRequired() throws Exception {
    int databaseSizeBeforeTest = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    // set the field null
    succursale.setIntSuccursale(null);

    // Create the Succursale, which fails.
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    restSuccursaleMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(succursaleDTO)))
      .andExpect(status().isBadRequest());

    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllSuccursales() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);

    // Get all the succursaleList
    restSuccursaleMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(succursale.getId())))
      .andExpect(jsonPath("$.[*].intSuccursale").value(hasItem(DEFAULT_INT_SUCCURSALE)));
  }

  @Test
  void getSuccursale() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);

    // Get the succursale
    restSuccursaleMockMvc
      .perform(get(ENTITY_API_URL_ID, succursale.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(succursale.getId()))
      .andExpect(jsonPath("$.intSuccursale").value(DEFAULT_INT_SUCCURSALE));
  }

  @Test
  void getNonExistingSuccursale() throws Exception {
    // Get the succursale
    restSuccursaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingSuccursale() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);

    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    succursaleSearchRepository.save(succursale);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());

    // Update the succursale
    Succursale updatedSuccursale = succursaleRepository.findById(succursale.getId()).get();
    updatedSuccursale.intSuccursale(UPDATED_INT_SUCCURSALE);
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(updatedSuccursale);

    restSuccursaleMockMvc
      .perform(
        put(ENTITY_API_URL_ID, succursaleDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(succursaleDTO))
      )
      .andExpect(status().isOk());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    Succursale testSuccursale = succursaleList.get(succursaleList.size() - 1);
    assertThat(testSuccursale.getIntSuccursale()).isEqualTo(UPDATED_INT_SUCCURSALE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Succursale> succursaleSearchList = IterableUtils.toList(succursaleSearchRepository.findAll());
        Succursale testSuccursaleSearch = succursaleSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testSuccursaleSearch.getIntSuccursale()).isEqualTo(UPDATED_INT_SUCCURSALE);
      });
  }

  @Test
  void putNonExistingSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(
        put(ENTITY_API_URL_ID, succursaleDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(succursaleDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(succursaleDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(succursaleDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateSuccursaleWithPatch() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);

    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();

    // Update the succursale using partial update
    Succursale partialUpdatedSuccursale = new Succursale();
    partialUpdatedSuccursale.setId(succursale.getId());

    partialUpdatedSuccursale.intSuccursale(UPDATED_INT_SUCCURSALE);

    restSuccursaleMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSuccursale.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSuccursale))
      )
      .andExpect(status().isOk());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    Succursale testSuccursale = succursaleList.get(succursaleList.size() - 1);
    assertThat(testSuccursale.getIntSuccursale()).isEqualTo(UPDATED_INT_SUCCURSALE);
  }

  @Test
  void fullUpdateSuccursaleWithPatch() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);

    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();

    // Update the succursale using partial update
    Succursale partialUpdatedSuccursale = new Succursale();
    partialUpdatedSuccursale.setId(succursale.getId());

    partialUpdatedSuccursale.intSuccursale(UPDATED_INT_SUCCURSALE);

    restSuccursaleMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSuccursale.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSuccursale))
      )
      .andExpect(status().isOk());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    Succursale testSuccursale = succursaleList.get(succursaleList.size() - 1);
    assertThat(testSuccursale.getIntSuccursale()).isEqualTo(UPDATED_INT_SUCCURSALE);
  }

  @Test
  void patchNonExistingSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, succursaleDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(succursaleDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(succursaleDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamSuccursale() throws Exception {
    int databaseSizeBeforeUpdate = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    succursale.setId(UUID.randomUUID().toString());

    // Create the Succursale
    SuccursaleDTO succursaleDTO = succursaleMapper.toDto(succursale);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSuccursaleMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(succursaleDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Succursale in the database
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteSuccursale() throws Exception {
    // Initialize the database
    succursaleRepository.save(succursale);
    succursaleRepository.save(succursale);
    succursaleSearchRepository.save(succursale);

    int databaseSizeBeforeDelete = succursaleRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the succursale
    restSuccursaleMockMvc
      .perform(delete(ENTITY_API_URL_ID, succursale.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Succursale> succursaleList = succursaleRepository.findAll();
    assertThat(succursaleList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(succursaleSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchSuccursale() throws Exception {
    // Initialize the database
    succursale = succursaleRepository.save(succursale);
    succursaleSearchRepository.save(succursale);

    // Search the succursale
    restSuccursaleMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + succursale.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(succursale.getId())))
      .andExpect(jsonPath("$.[*].intSuccursale").value(hasItem(DEFAULT_INT_SUCCURSALE)));
  }
}
