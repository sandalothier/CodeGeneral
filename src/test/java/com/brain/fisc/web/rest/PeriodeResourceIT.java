package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Periode;
import com.brain.fisc.repository.PeriodeRepository;
import com.brain.fisc.repository.search.PeriodeSearchRepository;
import com.brain.fisc.service.dto.PeriodeDTO;
import com.brain.fisc.service.mapper.PeriodeMapper;
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
 * Integration tests for the {@link PeriodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PeriodeResourceIT {

  private static final String DEFAULT_INT_PERIODE = "AAAAAAAAAA";
  private static final String UPDATED_INT_PERIODE = "BBBBBBBBBB";

  private static final LocalDate DEFAULT_DEBUT = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_DEBUT = LocalDate.now(ZoneId.systemDefault());

  private static final LocalDate DEFAULT_FIN = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_FIN = LocalDate.now(ZoneId.systemDefault());

  private static final String ENTITY_API_URL = "/api/periodes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/periodes";

  @Autowired
  private PeriodeRepository periodeRepository;

  @Autowired
  private PeriodeMapper periodeMapper;

  @Autowired
  private PeriodeSearchRepository periodeSearchRepository;

  @Autowired
  private MockMvc restPeriodeMockMvc;

  private Periode periode;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Periode createEntity() {
    Periode periode = new Periode().intPeriode(DEFAULT_INT_PERIODE).debut(DEFAULT_DEBUT).fin(DEFAULT_FIN);
    return periode;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Periode createUpdatedEntity() {
    Periode periode = new Periode().intPeriode(UPDATED_INT_PERIODE).debut(UPDATED_DEBUT).fin(UPDATED_FIN);
    return periode;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    periodeSearchRepository.deleteAll();
    assertThat(periodeSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    periodeRepository.deleteAll();
    periode = createEntity();
  }

  @Test
  void createPeriode() throws Exception {
    int databaseSizeBeforeCreate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);
    restPeriodeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeDTO)))
      .andExpect(status().isCreated());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Periode testPeriode = periodeList.get(periodeList.size() - 1);
    assertThat(testPeriode.getIntPeriode()).isEqualTo(DEFAULT_INT_PERIODE);
    assertThat(testPeriode.getDebut()).isEqualTo(DEFAULT_DEBUT);
    assertThat(testPeriode.getFin()).isEqualTo(DEFAULT_FIN);
  }

  @Test
  void createPeriodeWithExistingId() throws Exception {
    // Create the Periode with an existing ID
    periode.setId("existing_id");
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    int databaseSizeBeforeCreate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restPeriodeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllPeriodes() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);

    // Get all the periodeList
    restPeriodeMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(periode.getId())))
      .andExpect(jsonPath("$.[*].intPeriode").value(hasItem(DEFAULT_INT_PERIODE)))
      .andExpect(jsonPath("$.[*].debut").value(hasItem(DEFAULT_DEBUT.toString())))
      .andExpect(jsonPath("$.[*].fin").value(hasItem(DEFAULT_FIN.toString())));
  }

  @Test
  void getPeriode() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);

    // Get the periode
    restPeriodeMockMvc
      .perform(get(ENTITY_API_URL_ID, periode.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(periode.getId()))
      .andExpect(jsonPath("$.intPeriode").value(DEFAULT_INT_PERIODE))
      .andExpect(jsonPath("$.debut").value(DEFAULT_DEBUT.toString()))
      .andExpect(jsonPath("$.fin").value(DEFAULT_FIN.toString()));
  }

  @Test
  void getNonExistingPeriode() throws Exception {
    // Get the periode
    restPeriodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingPeriode() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);

    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    periodeSearchRepository.save(periode);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());

    // Update the periode
    Periode updatedPeriode = periodeRepository.findById(periode.getId()).get();
    updatedPeriode.intPeriode(UPDATED_INT_PERIODE).debut(UPDATED_DEBUT).fin(UPDATED_FIN);
    PeriodeDTO periodeDTO = periodeMapper.toDto(updatedPeriode);

    restPeriodeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, periodeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(periodeDTO))
      )
      .andExpect(status().isOk());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    Periode testPeriode = periodeList.get(periodeList.size() - 1);
    assertThat(testPeriode.getIntPeriode()).isEqualTo(UPDATED_INT_PERIODE);
    assertThat(testPeriode.getDebut()).isEqualTo(UPDATED_DEBUT);
    assertThat(testPeriode.getFin()).isEqualTo(UPDATED_FIN);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Periode> periodeSearchList = IterableUtils.toList(periodeSearchRepository.findAll());
        Periode testPeriodeSearch = periodeSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testPeriodeSearch.getIntPeriode()).isEqualTo(UPDATED_INT_PERIODE);
        assertThat(testPeriodeSearch.getDebut()).isEqualTo(UPDATED_DEBUT);
        assertThat(testPeriodeSearch.getFin()).isEqualTo(UPDATED_FIN);
      });
  }

  @Test
  void putNonExistingPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, periodeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(periodeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(periodeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(periodeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdatePeriodeWithPatch() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);

    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();

    // Update the periode using partial update
    Periode partialUpdatedPeriode = new Periode();
    partialUpdatedPeriode.setId(periode.getId());

    partialUpdatedPeriode.intPeriode(UPDATED_INT_PERIODE);

    restPeriodeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPeriode.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPeriode))
      )
      .andExpect(status().isOk());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    Periode testPeriode = periodeList.get(periodeList.size() - 1);
    assertThat(testPeriode.getIntPeriode()).isEqualTo(UPDATED_INT_PERIODE);
    assertThat(testPeriode.getDebut()).isEqualTo(DEFAULT_DEBUT);
    assertThat(testPeriode.getFin()).isEqualTo(DEFAULT_FIN);
  }

  @Test
  void fullUpdatePeriodeWithPatch() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);

    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();

    // Update the periode using partial update
    Periode partialUpdatedPeriode = new Periode();
    partialUpdatedPeriode.setId(periode.getId());

    partialUpdatedPeriode.intPeriode(UPDATED_INT_PERIODE).debut(UPDATED_DEBUT).fin(UPDATED_FIN);

    restPeriodeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPeriode.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPeriode))
      )
      .andExpect(status().isOk());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    Periode testPeriode = periodeList.get(periodeList.size() - 1);
    assertThat(testPeriode.getIntPeriode()).isEqualTo(UPDATED_INT_PERIODE);
    assertThat(testPeriode.getDebut()).isEqualTo(UPDATED_DEBUT);
    assertThat(testPeriode.getFin()).isEqualTo(UPDATED_FIN);
  }

  @Test
  void patchNonExistingPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, periodeDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(periodeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(periodeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamPeriode() throws Exception {
    int databaseSizeBeforeUpdate = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    periode.setId(UUID.randomUUID().toString());

    // Create the Periode
    PeriodeDTO periodeDTO = periodeMapper.toDto(periode);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPeriodeMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(periodeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Periode in the database
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deletePeriode() throws Exception {
    // Initialize the database
    periodeRepository.save(periode);
    periodeRepository.save(periode);
    periodeSearchRepository.save(periode);

    int databaseSizeBeforeDelete = periodeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the periode
    restPeriodeMockMvc
      .perform(delete(ENTITY_API_URL_ID, periode.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Periode> periodeList = periodeRepository.findAll();
    assertThat(periodeList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(periodeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchPeriode() throws Exception {
    // Initialize the database
    periode = periodeRepository.save(periode);
    periodeSearchRepository.save(periode);

    // Search the periode
    restPeriodeMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + periode.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(periode.getId())))
      .andExpect(jsonPath("$.[*].intPeriode").value(hasItem(DEFAULT_INT_PERIODE)))
      .andExpect(jsonPath("$.[*].debut").value(hasItem(DEFAULT_DEBUT.toString())))
      .andExpect(jsonPath("$.[*].fin").value(hasItem(DEFAULT_FIN.toString())));
  }
}
