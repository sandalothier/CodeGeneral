package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Pointage;
import com.brain.fisc.repository.PointageRepository;
import com.brain.fisc.repository.search.PointageSearchRepository;
import com.brain.fisc.service.dto.PointageDTO;
import com.brain.fisc.service.mapper.PointageMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PointageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PointageResourceIT {

  private static final LocalDate DEFAULT_HEUR_ARRIVEE = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_HEUR_ARRIVEE = LocalDate.now(ZoneId.systemDefault());

  private static final LocalDate DEFAULT_HEUR_DEPART = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_HEUR_DEPART = LocalDate.now(ZoneId.systemDefault());

  private static final String ENTITY_API_URL = "/api/pointages";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/pointages";

  @Autowired
  private PointageRepository pointageRepository;

  @Autowired
  private PointageMapper pointageMapper;

  @Autowired
  private PointageSearchRepository pointageSearchRepository;

  @Autowired
  private MockMvc restPointageMockMvc;

  private Pointage pointage;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Pointage createEntity() {
    Pointage pointage = new Pointage().heurArrivee(DEFAULT_HEUR_ARRIVEE).heurDepart(DEFAULT_HEUR_DEPART);
    return pointage;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Pointage createUpdatedEntity() {
    Pointage pointage = new Pointage().heurArrivee(UPDATED_HEUR_ARRIVEE).heurDepart(UPDATED_HEUR_DEPART);
    return pointage;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    pointageSearchRepository.deleteAll();
    assertThat(pointageSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    pointageRepository.deleteAll();
    pointage = createEntity();
  }

  @Test
  void createPointage() throws Exception {
    int databaseSizeBeforeCreate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);
    restPointageMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isCreated());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Pointage testPointage = pointageList.get(pointageList.size() - 1);
    assertThat(testPointage.getHeurArrivee()).isEqualTo(DEFAULT_HEUR_ARRIVEE);
    assertThat(testPointage.getHeurDepart()).isEqualTo(DEFAULT_HEUR_DEPART);
  }

  @Test
  void createPointageWithExistingId() throws Exception {
    // Create the Pointage with an existing ID
    pointage.setId("existing_id");
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    int databaseSizeBeforeCreate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restPointageMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkHeurArriveeIsRequired() throws Exception {
    int databaseSizeBeforeTest = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    // set the field null
    pointage.setHeurArrivee(null);

    // Create the Pointage, which fails.
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    restPointageMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isBadRequest());

    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkHeurDepartIsRequired() throws Exception {
    int databaseSizeBeforeTest = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    // set the field null
    pointage.setHeurDepart(null);

    // Create the Pointage, which fails.
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    restPointageMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isBadRequest());

    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllPointages() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);

    // Get all the pointageList
    restPointageMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(pointage.getId())))
      .andExpect(jsonPath("$.[*].heurArrivee").value(hasItem(DEFAULT_HEUR_ARRIVEE.toString())))
      .andExpect(jsonPath("$.[*].heurDepart").value(hasItem(DEFAULT_HEUR_DEPART.toString())));
  }

  @Test
  void getPointage() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);

    // Get the pointage
    restPointageMockMvc
      .perform(get(ENTITY_API_URL_ID, pointage.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(pointage.getId()))
      .andExpect(jsonPath("$.heurArrivee").value(DEFAULT_HEUR_ARRIVEE.toString()))
      .andExpect(jsonPath("$.heurDepart").value(DEFAULT_HEUR_DEPART.toString()));
  }

  @Test
  void getNonExistingPointage() throws Exception {
    // Get the pointage
    restPointageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingPointage() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);

    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    pointageSearchRepository.save(pointage);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());

    // Update the pointage
    Pointage updatedPointage = pointageRepository.findById(pointage.getId()).get();
    updatedPointage.heurArrivee(UPDATED_HEUR_ARRIVEE).heurDepart(UPDATED_HEUR_DEPART);
    PointageDTO pointageDTO = pointageMapper.toDto(updatedPointage);

    restPointageMockMvc
      .perform(
        put(ENTITY_API_URL_ID, pointageDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(pointageDTO))
      )
      .andExpect(status().isOk());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    Pointage testPointage = pointageList.get(pointageList.size() - 1);
    assertThat(testPointage.getHeurArrivee()).isEqualTo(UPDATED_HEUR_ARRIVEE);
    assertThat(testPointage.getHeurDepart()).isEqualTo(UPDATED_HEUR_DEPART);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Pointage> pointageSearchList = IterableUtils.toList(pointageSearchRepository.findAll());
        Pointage testPointageSearch = pointageSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testPointageSearch.getHeurArrivee()).isEqualTo(UPDATED_HEUR_ARRIVEE);
        assertThat(testPointageSearch.getHeurDepart()).isEqualTo(UPDATED_HEUR_DEPART);
      });
  }

  @Test
  void putNonExistingPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(
        put(ENTITY_API_URL_ID, pointageDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(pointageDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(pointageDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdatePointageWithPatch() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);

    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();

    // Update the pointage using partial update
    Pointage partialUpdatedPointage = new Pointage();
    partialUpdatedPointage.setId(pointage.getId());

    partialUpdatedPointage.heurArrivee(UPDATED_HEUR_ARRIVEE);

    restPointageMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPointage.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPointage))
      )
      .andExpect(status().isOk());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    Pointage testPointage = pointageList.get(pointageList.size() - 1);
    assertThat(testPointage.getHeurArrivee()).isEqualTo(UPDATED_HEUR_ARRIVEE);
    assertThat(testPointage.getHeurDepart()).isEqualTo(DEFAULT_HEUR_DEPART);
  }

  @Test
  void fullUpdatePointageWithPatch() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);

    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();

    // Update the pointage using partial update
    Pointage partialUpdatedPointage = new Pointage();
    partialUpdatedPointage.setId(pointage.getId());

    partialUpdatedPointage.heurArrivee(UPDATED_HEUR_ARRIVEE).heurDepart(UPDATED_HEUR_DEPART);

    restPointageMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPointage.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPointage))
      )
      .andExpect(status().isOk());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    Pointage testPointage = pointageList.get(pointageList.size() - 1);
    assertThat(testPointage.getHeurArrivee()).isEqualTo(UPDATED_HEUR_ARRIVEE);
    assertThat(testPointage.getHeurDepart()).isEqualTo(UPDATED_HEUR_DEPART);
  }

  @Test
  void patchNonExistingPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, pointageDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(pointageDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(pointageDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamPointage() throws Exception {
    int databaseSizeBeforeUpdate = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    pointage.setId(UUID.randomUUID().toString());

    // Create the Pointage
    PointageDTO pointageDTO = pointageMapper.toDto(pointage);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPointageMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pointageDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Pointage in the database
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deletePointage() throws Exception {
    // Initialize the database
    pointageRepository.save(pointage);
    pointageRepository.save(pointage);
    pointageSearchRepository.save(pointage);

    int databaseSizeBeforeDelete = pointageRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the pointage
    restPointageMockMvc
      .perform(delete(ENTITY_API_URL_ID, pointage.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Pointage> pointageList = pointageRepository.findAll();
    assertThat(pointageList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(pointageSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchPointage() throws Exception {
    // Initialize the database
    pointage = pointageRepository.save(pointage);
    pointageSearchRepository.save(pointage);

    // Search the pointage
    restPointageMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + pointage.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(pointage.getId())))
      .andExpect(jsonPath("$.[*].heurArrivee").value(hasItem(DEFAULT_HEUR_ARRIVEE.toString())))
      .andExpect(jsonPath("$.[*].heurDepart").value(hasItem(DEFAULT_HEUR_DEPART.toString())));
  }
}
