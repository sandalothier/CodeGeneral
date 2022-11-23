package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Conge;
import com.brain.fisc.repository.CongeRepository;
import com.brain.fisc.repository.search.CongeSearchRepository;
import com.brain.fisc.service.dto.CongeDTO;
import com.brain.fisc.service.mapper.CongeMapper;
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
 * Integration tests for the {@link CongeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CongeResourceIT {

  private static final String DEFAULT_REF_CONGE = "AAAAAAAAAA";
  private static final String UPDATED_REF_CONGE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/conges";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/conges";

  @Autowired
  private CongeRepository congeRepository;

  @Autowired
  private CongeMapper congeMapper;

  @Autowired
  private CongeSearchRepository congeSearchRepository;

  @Autowired
  private MockMvc restCongeMockMvc;

  private Conge conge;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Conge createEntity() {
    Conge conge = new Conge().refConge(DEFAULT_REF_CONGE);
    return conge;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Conge createUpdatedEntity() {
    Conge conge = new Conge().refConge(UPDATED_REF_CONGE);
    return conge;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    congeSearchRepository.deleteAll();
    assertThat(congeSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    congeRepository.deleteAll();
    conge = createEntity();
  }

  @Test
  void createConge() throws Exception {
    int databaseSizeBeforeCreate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);
    restCongeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(congeDTO)))
      .andExpect(status().isCreated());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Conge testConge = congeList.get(congeList.size() - 1);
    assertThat(testConge.getRefConge()).isEqualTo(DEFAULT_REF_CONGE);
  }

  @Test
  void createCongeWithExistingId() throws Exception {
    // Create the Conge with an existing ID
    conge.setId("existing_id");
    CongeDTO congeDTO = congeMapper.toDto(conge);

    int databaseSizeBeforeCreate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restCongeMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(congeDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllConges() throws Exception {
    // Initialize the database
    congeRepository.save(conge);

    // Get all the congeList
    restCongeMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId())))
      .andExpect(jsonPath("$.[*].refConge").value(hasItem(DEFAULT_REF_CONGE)));
  }

  @Test
  void getConge() throws Exception {
    // Initialize the database
    congeRepository.save(conge);

    // Get the conge
    restCongeMockMvc
      .perform(get(ENTITY_API_URL_ID, conge.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(conge.getId()))
      .andExpect(jsonPath("$.refConge").value(DEFAULT_REF_CONGE));
  }

  @Test
  void getNonExistingConge() throws Exception {
    // Get the conge
    restCongeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingConge() throws Exception {
    // Initialize the database
    congeRepository.save(conge);

    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    congeSearchRepository.save(conge);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());

    // Update the conge
    Conge updatedConge = congeRepository.findById(conge.getId()).get();
    updatedConge.refConge(UPDATED_REF_CONGE);
    CongeDTO congeDTO = congeMapper.toDto(updatedConge);

    restCongeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, congeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(congeDTO))
      )
      .andExpect(status().isOk());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    Conge testConge = congeList.get(congeList.size() - 1);
    assertThat(testConge.getRefConge()).isEqualTo(UPDATED_REF_CONGE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Conge> congeSearchList = IterableUtils.toList(congeSearchRepository.findAll());
        Conge testCongeSearch = congeSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testCongeSearch.getRefConge()).isEqualTo(UPDATED_REF_CONGE);
      });
  }

  @Test
  void putNonExistingConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, congeDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(congeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(congeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(congeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateCongeWithPatch() throws Exception {
    // Initialize the database
    congeRepository.save(conge);

    int databaseSizeBeforeUpdate = congeRepository.findAll().size();

    // Update the conge using partial update
    Conge partialUpdatedConge = new Conge();
    partialUpdatedConge.setId(conge.getId());

    restCongeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedConge.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConge))
      )
      .andExpect(status().isOk());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    Conge testConge = congeList.get(congeList.size() - 1);
    assertThat(testConge.getRefConge()).isEqualTo(DEFAULT_REF_CONGE);
  }

  @Test
  void fullUpdateCongeWithPatch() throws Exception {
    // Initialize the database
    congeRepository.save(conge);

    int databaseSizeBeforeUpdate = congeRepository.findAll().size();

    // Update the conge using partial update
    Conge partialUpdatedConge = new Conge();
    partialUpdatedConge.setId(conge.getId());

    partialUpdatedConge.refConge(UPDATED_REF_CONGE);

    restCongeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedConge.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConge))
      )
      .andExpect(status().isOk());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    Conge testConge = congeList.get(congeList.size() - 1);
    assertThat(testConge.getRefConge()).isEqualTo(UPDATED_REF_CONGE);
  }

  @Test
  void patchNonExistingConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, congeDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(congeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(congeDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamConge() throws Exception {
    int databaseSizeBeforeUpdate = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    conge.setId(UUID.randomUUID().toString());

    // Create the Conge
    CongeDTO congeDTO = congeMapper.toDto(conge);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCongeMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(congeDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Conge in the database
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteConge() throws Exception {
    // Initialize the database
    congeRepository.save(conge);
    congeRepository.save(conge);
    congeSearchRepository.save(conge);

    int databaseSizeBeforeDelete = congeRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the conge
    restCongeMockMvc.perform(delete(ENTITY_API_URL_ID, conge.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Conge> congeList = congeRepository.findAll();
    assertThat(congeList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(congeSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchConge() throws Exception {
    // Initialize the database
    conge = congeRepository.save(conge);
    congeSearchRepository.save(conge);

    // Search the conge
    restCongeMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + conge.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId())))
      .andExpect(jsonPath("$.[*].refConge").value(hasItem(DEFAULT_REF_CONGE)));
  }
}
