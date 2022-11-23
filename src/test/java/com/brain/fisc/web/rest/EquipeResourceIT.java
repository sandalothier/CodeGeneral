package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Equipe;
import com.brain.fisc.repository.EquipeRepository;
import com.brain.fisc.repository.search.EquipeSearchRepository;
import com.brain.fisc.service.dto.EquipeDTO;
import com.brain.fisc.service.mapper.EquipeMapper;
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
 * Integration tests for the {@link EquipeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EquipeResourceIT {

  private static final String DEFAULT_REF_EQUIPE = "AAAAAAAAAA";
  private static final String UPDATED_REF_EQUIPE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/equipes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/equipes";

  @Autowired
  private EquipeRepository equipeRepository;

  @Autowired
  private EquipeMapper equipeMapper;

  @Autowired
  private EquipeSearchRepository equipeSearchRepository;

  @Autowired
  private MockMvc restEquipeMockMvc;

  private Equipe equipe;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Equipe createEntity() {
    Equipe equipe = new Equipe().refEquipe(DEFAULT_REF_EQUIPE);
    return equipe;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Equipe createUpdatedEntity() {
    Equipe equipe = new Equipe().refEquipe(UPDATED_REF_EQUIPE);
    return equipe;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    equipeSearchRepository.deleteAll();
    assertThat(equipeSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    equipeRepository.deleteAll();
    equipe = createEntity();
  }

  @Test
  void createEquipe() throws Exception {
    int databaseSizeBeforeCreate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);
    restEquipeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
      .andExpect(status().isCreated());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Equipe testEquipe = equipeList.get(equipeList.size() - 1);
    assertThat(testEquipe.getRefEquipe()).isEqualTo(DEFAULT_REF_EQUIPE);
  }

  @Test
  void createEquipeWithExistingId() throws Exception {
    // Create the Equipe with an existing ID
    equipe.setId("existing_id");
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    int databaseSizeBeforeCreate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restEquipeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllEquipes() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);

    // Get all the equipeList
    restEquipeMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(equipe.getId())))
      .andExpect(jsonPath("$.[*].refEquipe").value(hasItem(DEFAULT_REF_EQUIPE)));
  }

  @Test
  void getEquipe() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);

    // Get the equipe
    restEquipeMockMvc
      .perform(get(ENTITY_API_URL_ID, equipe.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(equipe.getId()))
      .andExpect(jsonPath("$.refEquipe").value(DEFAULT_REF_EQUIPE));
  }

  @Test
  void getNonExistingEquipe() throws Exception {
    // Get the equipe
    restEquipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingEquipe() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);

    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    equipeSearchRepository.save(equipe);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());

    // Update the equipe
    Equipe updatedEquipe = equipeRepository.findById(equipe.getId()).get();
    updatedEquipe.refEquipe(UPDATED_REF_EQUIPE);
    EquipeDTO equipeDTO = equipeMapper.toDto(updatedEquipe);

    restEquipeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, equipeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
      )
      .andExpect(status().isOk());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    Equipe testEquipe = equipeList.get(equipeList.size() - 1);
    assertThat(testEquipe.getRefEquipe()).isEqualTo(UPDATED_REF_EQUIPE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Equipe> equipeSearchList = IterableUtils.toList(equipeSearchRepository.findAll());
        Equipe testEquipeSearch = equipeSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testEquipeSearch.getRefEquipe()).isEqualTo(UPDATED_REF_EQUIPE);
      });
  }

  @Test
  void putNonExistingEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, equipeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateEquipeWithPatch() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);

    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();

    // Update the equipe using partial update
    Equipe partialUpdatedEquipe = new Equipe();
    partialUpdatedEquipe.setId(equipe.getId());

    partialUpdatedEquipe.refEquipe(UPDATED_REF_EQUIPE);

    restEquipeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
      )
      .andExpect(status().isOk());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    Equipe testEquipe = equipeList.get(equipeList.size() - 1);
    assertThat(testEquipe.getRefEquipe()).isEqualTo(UPDATED_REF_EQUIPE);
  }

  @Test
  void fullUpdateEquipeWithPatch() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);

    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();

    // Update the equipe using partial update
    Equipe partialUpdatedEquipe = new Equipe();
    partialUpdatedEquipe.setId(equipe.getId());

    partialUpdatedEquipe.refEquipe(UPDATED_REF_EQUIPE);

    restEquipeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
      )
      .andExpect(status().isOk());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    Equipe testEquipe = equipeList.get(equipeList.size() - 1);
    assertThat(testEquipe.getRefEquipe()).isEqualTo(UPDATED_REF_EQUIPE);
  }

  @Test
  void patchNonExistingEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, equipeDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamEquipe() throws Exception {
    int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    equipe.setId(UUID.randomUUID().toString());

    // Create the Equipe
    EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restEquipeMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Equipe in the database
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteEquipe() throws Exception {
    // Initialize the database
    equipeRepository.save(equipe);
    equipeRepository.save(equipe);
    equipeSearchRepository.save(equipe);

    int databaseSizeBeforeDelete = equipeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the equipe
    restEquipeMockMvc
      .perform(delete(ENTITY_API_URL_ID, equipe.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Equipe> equipeList = equipeRepository.findAll();
    assertThat(equipeList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(equipeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchEquipe() throws Exception {
    // Initialize the database
    equipe = equipeRepository.save(equipe);
    equipeSearchRepository.save(equipe);

    // Search the equipe
    restEquipeMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + equipe.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(equipe.getId())))
      .andExpect(jsonPath("$.[*].refEquipe").value(hasItem(DEFAULT_REF_EQUIPE)));
  }
}
