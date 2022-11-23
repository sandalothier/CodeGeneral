package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Societe;
import com.brain.fisc.repository.SocieteRepository;
import com.brain.fisc.repository.search.SocieteSearchRepository;
import com.brain.fisc.service.dto.SocieteDTO;
import com.brain.fisc.service.mapper.SocieteMapper;
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
 * Integration tests for the {@link SocieteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SocieteResourceIT {

  private static final String DEFAULT_INT_SOCIETE = "AAAAAAAAAA";
  private static final String UPDATED_INT_SOCIETE = "BBBBBBBBBB";

  private static final String DEFAULT_SIGLE = "AAAAAAAAAA";
  private static final String UPDATED_SIGLE = "BBBBBBBBBB";

  private static final String DEFAULT_LOGO = "AAAAAAAAAA";
  private static final String UPDATED_LOGO = "BBBBBBBBBB";

  private static final String DEFAULT_SIEGE = "AAAAAAAAAA";
  private static final String UPDATED_SIEGE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/societes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/societes";

  @Autowired
  private SocieteRepository societeRepository;

  @Autowired
  private SocieteMapper societeMapper;

  @Autowired
  private SocieteSearchRepository societeSearchRepository;

  @Autowired
  private MockMvc restSocieteMockMvc;

  private Societe societe;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Societe createEntity() {
    Societe societe = new Societe().intSociete(DEFAULT_INT_SOCIETE).sigle(DEFAULT_SIGLE).logo(DEFAULT_LOGO).siege(DEFAULT_SIEGE);
    return societe;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Societe createUpdatedEntity() {
    Societe societe = new Societe().intSociete(UPDATED_INT_SOCIETE).sigle(UPDATED_SIGLE).logo(UPDATED_LOGO).siege(UPDATED_SIEGE);
    return societe;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    societeSearchRepository.deleteAll();
    assertThat(societeSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    societeRepository.deleteAll();
    societe = createEntity();
  }

  @Test
  void createSociete() throws Exception {
    int databaseSizeBeforeCreate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);
    restSocieteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isCreated());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Societe testSociete = societeList.get(societeList.size() - 1);
    assertThat(testSociete.getIntSociete()).isEqualTo(DEFAULT_INT_SOCIETE);
    assertThat(testSociete.getSigle()).isEqualTo(DEFAULT_SIGLE);
    assertThat(testSociete.getLogo()).isEqualTo(DEFAULT_LOGO);
    assertThat(testSociete.getSiege()).isEqualTo(DEFAULT_SIEGE);
  }

  @Test
  void createSocieteWithExistingId() throws Exception {
    // Create the Societe with an existing ID
    societe.setId("existing_id");
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    int databaseSizeBeforeCreate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restSocieteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkIntSocieteIsRequired() throws Exception {
    int databaseSizeBeforeTest = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    // set the field null
    societe.setIntSociete(null);

    // Create the Societe, which fails.
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    restSocieteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isBadRequest());

    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkLogoIsRequired() throws Exception {
    int databaseSizeBeforeTest = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    // set the field null
    societe.setLogo(null);

    // Create the Societe, which fails.
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    restSocieteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isBadRequest());

    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllSocietes() throws Exception {
    // Initialize the database
    societeRepository.save(societe);

    // Get all the societeList
    restSocieteMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(societe.getId())))
      .andExpect(jsonPath("$.[*].intSociete").value(hasItem(DEFAULT_INT_SOCIETE)))
      .andExpect(jsonPath("$.[*].sigle").value(hasItem(DEFAULT_SIGLE)))
      .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO)))
      .andExpect(jsonPath("$.[*].siege").value(hasItem(DEFAULT_SIEGE)));
  }

  @Test
  void getSociete() throws Exception {
    // Initialize the database
    societeRepository.save(societe);

    // Get the societe
    restSocieteMockMvc
      .perform(get(ENTITY_API_URL_ID, societe.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(societe.getId()))
      .andExpect(jsonPath("$.intSociete").value(DEFAULT_INT_SOCIETE))
      .andExpect(jsonPath("$.sigle").value(DEFAULT_SIGLE))
      .andExpect(jsonPath("$.logo").value(DEFAULT_LOGO))
      .andExpect(jsonPath("$.siege").value(DEFAULT_SIEGE));
  }

  @Test
  void getNonExistingSociete() throws Exception {
    // Get the societe
    restSocieteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingSociete() throws Exception {
    // Initialize the database
    societeRepository.save(societe);

    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    societeSearchRepository.save(societe);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());

    // Update the societe
    Societe updatedSociete = societeRepository.findById(societe.getId()).get();
    updatedSociete.intSociete(UPDATED_INT_SOCIETE).sigle(UPDATED_SIGLE).logo(UPDATED_LOGO).siege(UPDATED_SIEGE);
    SocieteDTO societeDTO = societeMapper.toDto(updatedSociete);

    restSocieteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, societeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(societeDTO))
      )
      .andExpect(status().isOk());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    Societe testSociete = societeList.get(societeList.size() - 1);
    assertThat(testSociete.getIntSociete()).isEqualTo(UPDATED_INT_SOCIETE);
    assertThat(testSociete.getSigle()).isEqualTo(UPDATED_SIGLE);
    assertThat(testSociete.getLogo()).isEqualTo(UPDATED_LOGO);
    assertThat(testSociete.getSiege()).isEqualTo(UPDATED_SIEGE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Societe> societeSearchList = IterableUtils.toList(societeSearchRepository.findAll());
        Societe testSocieteSearch = societeSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testSocieteSearch.getIntSociete()).isEqualTo(UPDATED_INT_SOCIETE);
        assertThat(testSocieteSearch.getSigle()).isEqualTo(UPDATED_SIGLE);
        assertThat(testSocieteSearch.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testSocieteSearch.getSiege()).isEqualTo(UPDATED_SIEGE);
      });
  }

  @Test
  void putNonExistingSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, societeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(societeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(societeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateSocieteWithPatch() throws Exception {
    // Initialize the database
    societeRepository.save(societe);

    int databaseSizeBeforeUpdate = societeRepository.findAll().size();

    // Update the societe using partial update
    Societe partialUpdatedSociete = new Societe();
    partialUpdatedSociete.setId(societe.getId());

    partialUpdatedSociete.intSociete(UPDATED_INT_SOCIETE).sigle(UPDATED_SIGLE).logo(UPDATED_LOGO);

    restSocieteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSociete.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSociete))
      )
      .andExpect(status().isOk());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    Societe testSociete = societeList.get(societeList.size() - 1);
    assertThat(testSociete.getIntSociete()).isEqualTo(UPDATED_INT_SOCIETE);
    assertThat(testSociete.getSigle()).isEqualTo(UPDATED_SIGLE);
    assertThat(testSociete.getLogo()).isEqualTo(UPDATED_LOGO);
    assertThat(testSociete.getSiege()).isEqualTo(DEFAULT_SIEGE);
  }

  @Test
  void fullUpdateSocieteWithPatch() throws Exception {
    // Initialize the database
    societeRepository.save(societe);

    int databaseSizeBeforeUpdate = societeRepository.findAll().size();

    // Update the societe using partial update
    Societe partialUpdatedSociete = new Societe();
    partialUpdatedSociete.setId(societe.getId());

    partialUpdatedSociete.intSociete(UPDATED_INT_SOCIETE).sigle(UPDATED_SIGLE).logo(UPDATED_LOGO).siege(UPDATED_SIEGE);

    restSocieteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedSociete.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSociete))
      )
      .andExpect(status().isOk());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    Societe testSociete = societeList.get(societeList.size() - 1);
    assertThat(testSociete.getIntSociete()).isEqualTo(UPDATED_INT_SOCIETE);
    assertThat(testSociete.getSigle()).isEqualTo(UPDATED_SIGLE);
    assertThat(testSociete.getLogo()).isEqualTo(UPDATED_LOGO);
    assertThat(testSociete.getSiege()).isEqualTo(UPDATED_SIEGE);
  }

  @Test
  void patchNonExistingSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, societeDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(societeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(societeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamSociete() throws Exception {
    int databaseSizeBeforeUpdate = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    societe.setId(UUID.randomUUID().toString());

    // Create the Societe
    SocieteDTO societeDTO = societeMapper.toDto(societe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restSocieteMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(societeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Societe in the database
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteSociete() throws Exception {
    // Initialize the database
    societeRepository.save(societe);
    societeRepository.save(societe);
    societeSearchRepository.save(societe);

    int databaseSizeBeforeDelete = societeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the societe
    restSocieteMockMvc
      .perform(delete(ENTITY_API_URL_ID, societe.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Societe> societeList = societeRepository.findAll();
    assertThat(societeList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(societeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchSociete() throws Exception {
    // Initialize the database
    societe = societeRepository.save(societe);
    societeSearchRepository.save(societe);

    // Search the societe
    restSocieteMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + societe.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(societe.getId())))
      .andExpect(jsonPath("$.[*].intSociete").value(hasItem(DEFAULT_INT_SOCIETE)))
      .andExpect(jsonPath("$.[*].sigle").value(hasItem(DEFAULT_SIGLE)))
      .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO)))
      .andExpect(jsonPath("$.[*].siege").value(hasItem(DEFAULT_SIEGE)));
  }
}
