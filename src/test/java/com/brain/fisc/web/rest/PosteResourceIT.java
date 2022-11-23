package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Poste;
import com.brain.fisc.repository.PosteRepository;
import com.brain.fisc.repository.search.PosteSearchRepository;
import com.brain.fisc.service.dto.PosteDTO;
import com.brain.fisc.service.mapper.PosteMapper;
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
 * Integration tests for the {@link PosteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PosteResourceIT {

  private static final String DEFAULT_INT_POSTE = "AAAAAAAAAA";
  private static final String UPDATED_INT_POSTE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/postes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/postes";

  @Autowired
  private PosteRepository posteRepository;

  @Autowired
  private PosteMapper posteMapper;

  @Autowired
  private PosteSearchRepository posteSearchRepository;

  @Autowired
  private MockMvc restPosteMockMvc;

  private Poste poste;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Poste createEntity() {
    Poste poste = new Poste().intPoste(DEFAULT_INT_POSTE);
    return poste;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Poste createUpdatedEntity() {
    Poste poste = new Poste().intPoste(UPDATED_INT_POSTE);
    return poste;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    posteSearchRepository.deleteAll();
    assertThat(posteSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    posteRepository.deleteAll();
    poste = createEntity();
  }

  @Test
  void createPoste() throws Exception {
    int databaseSizeBeforeCreate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);
    restPosteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
      .andExpect(status().isCreated());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Poste testPoste = posteList.get(posteList.size() - 1);
    assertThat(testPoste.getIntPoste()).isEqualTo(DEFAULT_INT_POSTE);
  }

  @Test
  void createPosteWithExistingId() throws Exception {
    // Create the Poste with an existing ID
    poste.setId("existing_id");
    PosteDTO posteDTO = posteMapper.toDto(poste);

    int databaseSizeBeforeCreate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restPosteMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllPostes() throws Exception {
    // Initialize the database
    posteRepository.save(poste);

    // Get all the posteList
    restPosteMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId())))
      .andExpect(jsonPath("$.[*].intPoste").value(hasItem(DEFAULT_INT_POSTE)));
  }

  @Test
  void getPoste() throws Exception {
    // Initialize the database
    posteRepository.save(poste);

    // Get the poste
    restPosteMockMvc
      .perform(get(ENTITY_API_URL_ID, poste.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(poste.getId()))
      .andExpect(jsonPath("$.intPoste").value(DEFAULT_INT_POSTE));
  }

  @Test
  void getNonExistingPoste() throws Exception {
    // Get the poste
    restPosteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingPoste() throws Exception {
    // Initialize the database
    posteRepository.save(poste);

    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    posteSearchRepository.save(poste);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());

    // Update the poste
    Poste updatedPoste = posteRepository.findById(poste.getId()).get();
    updatedPoste.intPoste(UPDATED_INT_POSTE);
    PosteDTO posteDTO = posteMapper.toDto(updatedPoste);

    restPosteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, posteDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(posteDTO))
      )
      .andExpect(status().isOk());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    Poste testPoste = posteList.get(posteList.size() - 1);
    assertThat(testPoste.getIntPoste()).isEqualTo(UPDATED_INT_POSTE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Poste> posteSearchList = IterableUtils.toList(posteSearchRepository.findAll());
        Poste testPosteSearch = posteSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testPosteSearch.getIntPoste()).isEqualTo(UPDATED_INT_POSTE);
      });
  }

  @Test
  void putNonExistingPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, posteDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(posteDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(posteDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdatePosteWithPatch() throws Exception {
    // Initialize the database
    posteRepository.save(poste);

    int databaseSizeBeforeUpdate = posteRepository.findAll().size();

    // Update the poste using partial update
    Poste partialUpdatedPoste = new Poste();
    partialUpdatedPoste.setId(poste.getId());

    partialUpdatedPoste.intPoste(UPDATED_INT_POSTE);

    restPosteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
      )
      .andExpect(status().isOk());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    Poste testPoste = posteList.get(posteList.size() - 1);
    assertThat(testPoste.getIntPoste()).isEqualTo(UPDATED_INT_POSTE);
  }

  @Test
  void fullUpdatePosteWithPatch() throws Exception {
    // Initialize the database
    posteRepository.save(poste);

    int databaseSizeBeforeUpdate = posteRepository.findAll().size();

    // Update the poste using partial update
    Poste partialUpdatedPoste = new Poste();
    partialUpdatedPoste.setId(poste.getId());

    partialUpdatedPoste.intPoste(UPDATED_INT_POSTE);

    restPosteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
      )
      .andExpect(status().isOk());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    Poste testPoste = posteList.get(posteList.size() - 1);
    assertThat(testPoste.getIntPoste()).isEqualTo(UPDATED_INT_POSTE);
  }

  @Test
  void patchNonExistingPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, posteDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(posteDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(posteDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamPoste() throws Exception {
    int databaseSizeBeforeUpdate = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    poste.setId(UUID.randomUUID().toString());

    // Create the Poste
    PosteDTO posteDTO = posteMapper.toDto(poste);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPosteMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(posteDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Poste in the database
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deletePoste() throws Exception {
    // Initialize the database
    posteRepository.save(poste);
    posteRepository.save(poste);
    posteSearchRepository.save(poste);

    int databaseSizeBeforeDelete = posteRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the poste
    restPosteMockMvc.perform(delete(ENTITY_API_URL_ID, poste.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Poste> posteList = posteRepository.findAll();
    assertThat(posteList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(posteSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchPoste() throws Exception {
    // Initialize the database
    poste = posteRepository.save(poste);
    posteSearchRepository.save(poste);

    // Search the poste
    restPosteMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + poste.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId())))
      .andExpect(jsonPath("$.[*].intPoste").value(hasItem(DEFAULT_INT_POSTE)));
  }
}
