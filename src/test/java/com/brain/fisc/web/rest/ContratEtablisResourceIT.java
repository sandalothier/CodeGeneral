package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.ContratEtablis;
import com.brain.fisc.repository.ContratEtablisRepository;
import com.brain.fisc.repository.search.ContratEtablisSearchRepository;
import com.brain.fisc.service.dto.ContratEtablisDTO;
import com.brain.fisc.service.mapper.ContratEtablisMapper;
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
 * Integration tests for the {@link ContratEtablisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContratEtablisResourceIT {

  private static final String DEFAULT_REF_CONTRAT = "AAAAAAAAAA";
  private static final String UPDATED_REF_CONTRAT = "BBBBBBBBBB";

  private static final LocalDate DEFAULT_DATE_ETABLISSEMENT = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_DATE_ETABLISSEMENT = LocalDate.now(ZoneId.systemDefault());

  private static final String ENTITY_API_URL = "/api/contrat-etablis";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/contrat-etablis";

  @Autowired
  private ContratEtablisRepository contratEtablisRepository;

  @Autowired
  private ContratEtablisMapper contratEtablisMapper;

  @Autowired
  private ContratEtablisSearchRepository contratEtablisSearchRepository;

  @Autowired
  private MockMvc restContratEtablisMockMvc;

  private ContratEtablis contratEtablis;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static ContratEtablis createEntity() {
    ContratEtablis contratEtablis = new ContratEtablis().refContrat(DEFAULT_REF_CONTRAT).dateEtablissement(DEFAULT_DATE_ETABLISSEMENT);
    return contratEtablis;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static ContratEtablis createUpdatedEntity() {
    ContratEtablis contratEtablis = new ContratEtablis().refContrat(UPDATED_REF_CONTRAT).dateEtablissement(UPDATED_DATE_ETABLISSEMENT);
    return contratEtablis;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    contratEtablisSearchRepository.deleteAll();
    assertThat(contratEtablisSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    contratEtablisRepository.deleteAll();
    contratEtablis = createEntity();
  }

  @Test
  void createContratEtablis() throws Exception {
    int databaseSizeBeforeCreate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);
    restContratEtablisMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO)))
      .andExpect(status().isCreated());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    ContratEtablis testContratEtablis = contratEtablisList.get(contratEtablisList.size() - 1);
    assertThat(testContratEtablis.getRefContrat()).isEqualTo(DEFAULT_REF_CONTRAT);
    assertThat(testContratEtablis.getDateEtablissement()).isEqualTo(DEFAULT_DATE_ETABLISSEMENT);
  }

  @Test
  void createContratEtablisWithExistingId() throws Exception {
    // Create the ContratEtablis with an existing ID
    contratEtablis.setId("existing_id");
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    int databaseSizeBeforeCreate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restContratEtablisMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO)))
      .andExpect(status().isBadRequest());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkRefContratIsRequired() throws Exception {
    int databaseSizeBeforeTest = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    // set the field null
    contratEtablis.setRefContrat(null);

    // Create the ContratEtablis, which fails.
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    restContratEtablisMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO)))
      .andExpect(status().isBadRequest());

    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllContratEtablis() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);

    // Get all the contratEtablisList
    restContratEtablisMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(contratEtablis.getId())))
      .andExpect(jsonPath("$.[*].refContrat").value(hasItem(DEFAULT_REF_CONTRAT)))
      .andExpect(jsonPath("$.[*].dateEtablissement").value(hasItem(DEFAULT_DATE_ETABLISSEMENT.toString())));
  }

  @Test
  void getContratEtablis() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);

    // Get the contratEtablis
    restContratEtablisMockMvc
      .perform(get(ENTITY_API_URL_ID, contratEtablis.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(contratEtablis.getId()))
      .andExpect(jsonPath("$.refContrat").value(DEFAULT_REF_CONTRAT))
      .andExpect(jsonPath("$.dateEtablissement").value(DEFAULT_DATE_ETABLISSEMENT.toString()));
  }

  @Test
  void getNonExistingContratEtablis() throws Exception {
    // Get the contratEtablis
    restContratEtablisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingContratEtablis() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);

    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    contratEtablisSearchRepository.save(contratEtablis);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());

    // Update the contratEtablis
    ContratEtablis updatedContratEtablis = contratEtablisRepository.findById(contratEtablis.getId()).get();
    updatedContratEtablis.refContrat(UPDATED_REF_CONTRAT).dateEtablissement(UPDATED_DATE_ETABLISSEMENT);
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(updatedContratEtablis);

    restContratEtablisMockMvc
      .perform(
        put(ENTITY_API_URL_ID, contratEtablisDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isOk());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    ContratEtablis testContratEtablis = contratEtablisList.get(contratEtablisList.size() - 1);
    assertThat(testContratEtablis.getRefContrat()).isEqualTo(UPDATED_REF_CONTRAT);
    assertThat(testContratEtablis.getDateEtablissement()).isEqualTo(UPDATED_DATE_ETABLISSEMENT);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<ContratEtablis> contratEtablisSearchList = IterableUtils.toList(contratEtablisSearchRepository.findAll());
        ContratEtablis testContratEtablisSearch = contratEtablisSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testContratEtablisSearch.getRefContrat()).isEqualTo(UPDATED_REF_CONTRAT);
        assertThat(testContratEtablisSearch.getDateEtablissement()).isEqualTo(UPDATED_DATE_ETABLISSEMENT);
      });
  }

  @Test
  void putNonExistingContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(
        put(ENTITY_API_URL_ID, contratEtablisDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateContratEtablisWithPatch() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);

    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();

    // Update the contratEtablis using partial update
    ContratEtablis partialUpdatedContratEtablis = new ContratEtablis();
    partialUpdatedContratEtablis.setId(contratEtablis.getId());

    partialUpdatedContratEtablis.refContrat(UPDATED_REF_CONTRAT);

    restContratEtablisMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedContratEtablis.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContratEtablis))
      )
      .andExpect(status().isOk());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    ContratEtablis testContratEtablis = contratEtablisList.get(contratEtablisList.size() - 1);
    assertThat(testContratEtablis.getRefContrat()).isEqualTo(UPDATED_REF_CONTRAT);
    assertThat(testContratEtablis.getDateEtablissement()).isEqualTo(DEFAULT_DATE_ETABLISSEMENT);
  }

  @Test
  void fullUpdateContratEtablisWithPatch() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);

    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();

    // Update the contratEtablis using partial update
    ContratEtablis partialUpdatedContratEtablis = new ContratEtablis();
    partialUpdatedContratEtablis.setId(contratEtablis.getId());

    partialUpdatedContratEtablis.refContrat(UPDATED_REF_CONTRAT).dateEtablissement(UPDATED_DATE_ETABLISSEMENT);

    restContratEtablisMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedContratEtablis.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContratEtablis))
      )
      .andExpect(status().isOk());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    ContratEtablis testContratEtablis = contratEtablisList.get(contratEtablisList.size() - 1);
    assertThat(testContratEtablis.getRefContrat()).isEqualTo(UPDATED_REF_CONTRAT);
    assertThat(testContratEtablis.getDateEtablissement()).isEqualTo(UPDATED_DATE_ETABLISSEMENT);
  }

  @Test
  void patchNonExistingContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, contratEtablisDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamContratEtablis() throws Exception {
    int databaseSizeBeforeUpdate = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    contratEtablis.setId(UUID.randomUUID().toString());

    // Create the ContratEtablis
    ContratEtablisDTO contratEtablisDTO = contratEtablisMapper.toDto(contratEtablis);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restContratEtablisMockMvc
      .perform(
        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(contratEtablisDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the ContratEtablis in the database
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteContratEtablis() throws Exception {
    // Initialize the database
    contratEtablisRepository.save(contratEtablis);
    contratEtablisRepository.save(contratEtablis);
    contratEtablisSearchRepository.save(contratEtablis);

    int databaseSizeBeforeDelete = contratEtablisRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the contratEtablis
    restContratEtablisMockMvc
      .perform(delete(ENTITY_API_URL_ID, contratEtablis.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<ContratEtablis> contratEtablisList = contratEtablisRepository.findAll();
    assertThat(contratEtablisList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(contratEtablisSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchContratEtablis() throws Exception {
    // Initialize the database
    contratEtablis = contratEtablisRepository.save(contratEtablis);
    contratEtablisSearchRepository.save(contratEtablis);

    // Search the contratEtablis
    restContratEtablisMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + contratEtablis.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(contratEtablis.getId())))
      .andExpect(jsonPath("$.[*].refContrat").value(hasItem(DEFAULT_REF_CONTRAT)))
      .andExpect(jsonPath("$.[*].dateEtablissement").value(hasItem(DEFAULT_DATE_ETABLISSEMENT.toString())));
  }
}
