package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Diplome;
import com.brain.fisc.repository.DiplomeRepository;
import com.brain.fisc.repository.search.DiplomeSearchRepository;
import com.brain.fisc.service.dto.DiplomeDTO;
import com.brain.fisc.service.mapper.DiplomeMapper;
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
 * Integration tests for the {@link DiplomeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiplomeResourceIT {

  private static final String DEFAULT_CODE_DIPLOME = "AAAAAAAAAA";
  private static final String UPDATED_CODE_DIPLOME = "BBBBBBBBBB";

  private static final String DEFAULT_INT_DIPLOME = "AAAAAAAAAA";
  private static final String UPDATED_INT_DIPLOME = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/diplomes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/diplomes";

  @Autowired
  private DiplomeRepository diplomeRepository;

  @Autowired
  private DiplomeMapper diplomeMapper;

  @Autowired
  private DiplomeSearchRepository diplomeSearchRepository;

  @Autowired
  private MockMvc restDiplomeMockMvc;

  private Diplome diplome;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Diplome createEntity() {
    Diplome diplome = new Diplome().codeDiplome(DEFAULT_CODE_DIPLOME).intDiplome(DEFAULT_INT_DIPLOME);
    return diplome;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Diplome createUpdatedEntity() {
    Diplome diplome = new Diplome().codeDiplome(UPDATED_CODE_DIPLOME).intDiplome(UPDATED_INT_DIPLOME);
    return diplome;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    diplomeSearchRepository.deleteAll();
    assertThat(diplomeSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    diplomeRepository.deleteAll();
    diplome = createEntity();
  }

  @Test
  void createDiplome() throws Exception {
    int databaseSizeBeforeCreate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);
    restDiplomeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
      .andExpect(status().isCreated());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
    assertThat(testDiplome.getCodeDiplome()).isEqualTo(DEFAULT_CODE_DIPLOME);
    assertThat(testDiplome.getIntDiplome()).isEqualTo(DEFAULT_INT_DIPLOME);
  }

  @Test
  void createDiplomeWithExistingId() throws Exception {
    // Create the Diplome with an existing ID
    diplome.setId("existing_id");
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    int databaseSizeBeforeCreate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restDiplomeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkCodeDiplomeIsRequired() throws Exception {
    int databaseSizeBeforeTest = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    // set the field null
    diplome.setCodeDiplome(null);

    // Create the Diplome, which fails.
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    restDiplomeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
      .andExpect(status().isBadRequest());

    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllDiplomes() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);

    // Get all the diplomeList
    restDiplomeMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(diplome.getId())))
      .andExpect(jsonPath("$.[*].codeDiplome").value(hasItem(DEFAULT_CODE_DIPLOME)))
      .andExpect(jsonPath("$.[*].intDiplome").value(hasItem(DEFAULT_INT_DIPLOME)));
  }

  @Test
  void getDiplome() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);

    // Get the diplome
    restDiplomeMockMvc
      .perform(get(ENTITY_API_URL_ID, diplome.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(diplome.getId()))
      .andExpect(jsonPath("$.codeDiplome").value(DEFAULT_CODE_DIPLOME))
      .andExpect(jsonPath("$.intDiplome").value(DEFAULT_INT_DIPLOME));
  }

  @Test
  void getNonExistingDiplome() throws Exception {
    // Get the diplome
    restDiplomeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingDiplome() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);

    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    diplomeSearchRepository.save(diplome);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());

    // Update the diplome
    Diplome updatedDiplome = diplomeRepository.findById(diplome.getId()).get();
    updatedDiplome.codeDiplome(UPDATED_CODE_DIPLOME).intDiplome(UPDATED_INT_DIPLOME);
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(updatedDiplome);

    restDiplomeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, diplomeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(diplomeDTO))
      )
      .andExpect(status().isOk());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
    assertThat(testDiplome.getCodeDiplome()).isEqualTo(UPDATED_CODE_DIPLOME);
    assertThat(testDiplome.getIntDiplome()).isEqualTo(UPDATED_INT_DIPLOME);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Diplome> diplomeSearchList = IterableUtils.toList(diplomeSearchRepository.findAll());
        Diplome testDiplomeSearch = diplomeSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testDiplomeSearch.getCodeDiplome()).isEqualTo(UPDATED_CODE_DIPLOME);
        assertThat(testDiplomeSearch.getIntDiplome()).isEqualTo(UPDATED_INT_DIPLOME);
      });
  }

  @Test
  void putNonExistingDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, diplomeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(diplomeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(diplomeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateDiplomeWithPatch() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);

    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();

    // Update the diplome using partial update
    Diplome partialUpdatedDiplome = new Diplome();
    partialUpdatedDiplome.setId(diplome.getId());

    restDiplomeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedDiplome.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiplome))
      )
      .andExpect(status().isOk());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
    assertThat(testDiplome.getCodeDiplome()).isEqualTo(DEFAULT_CODE_DIPLOME);
    assertThat(testDiplome.getIntDiplome()).isEqualTo(DEFAULT_INT_DIPLOME);
  }

  @Test
  void fullUpdateDiplomeWithPatch() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);

    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();

    // Update the diplome using partial update
    Diplome partialUpdatedDiplome = new Diplome();
    partialUpdatedDiplome.setId(diplome.getId());

    partialUpdatedDiplome.codeDiplome(UPDATED_CODE_DIPLOME).intDiplome(UPDATED_INT_DIPLOME);

    restDiplomeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedDiplome.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiplome))
      )
      .andExpect(status().isOk());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
    assertThat(testDiplome.getCodeDiplome()).isEqualTo(UPDATED_CODE_DIPLOME);
    assertThat(testDiplome.getIntDiplome()).isEqualTo(UPDATED_INT_DIPLOME);
  }

  @Test
  void patchNonExistingDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, diplomeDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(diplomeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(diplomeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamDiplome() throws Exception {
    int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    diplome.setId(UUID.randomUUID().toString());

    // Create the Diplome
    DiplomeDTO diplomeDTO = diplomeMapper.toDto(diplome);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDiplomeMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Diplome in the database
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteDiplome() throws Exception {
    // Initialize the database
    diplomeRepository.save(diplome);
    diplomeRepository.save(diplome);
    diplomeSearchRepository.save(diplome);

    int databaseSizeBeforeDelete = diplomeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the diplome
    restDiplomeMockMvc
      .perform(delete(ENTITY_API_URL_ID, diplome.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Diplome> diplomeList = diplomeRepository.findAll();
    assertThat(diplomeList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(diplomeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchDiplome() throws Exception {
    // Initialize the database
    diplome = diplomeRepository.save(diplome);
    diplomeSearchRepository.save(diplome);

    // Search the diplome
    restDiplomeMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + diplome.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(diplome.getId())))
      .andExpect(jsonPath("$.[*].codeDiplome").value(hasItem(DEFAULT_CODE_DIPLOME)))
      .andExpect(jsonPath("$.[*].intDiplome").value(hasItem(DEFAULT_INT_DIPLOME)));
  }
}
