package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.BulletinPaie;
import com.brain.fisc.repository.BulletinPaieRepository;
import com.brain.fisc.repository.search.BulletinPaieSearchRepository;
import com.brain.fisc.service.dto.BulletinPaieDTO;
import com.brain.fisc.service.mapper.BulletinPaieMapper;
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
 * Integration tests for the {@link BulletinPaieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BulletinPaieResourceIT {

  private static final String DEFAULT_REF_BULLETIN = "AAAAAAAAAA";
  private static final String UPDATED_REF_BULLETIN = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/bulletin-paies";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/bulletin-paies";

  @Autowired
  private BulletinPaieRepository bulletinPaieRepository;

  @Autowired
  private BulletinPaieMapper bulletinPaieMapper;

  @Autowired
  private BulletinPaieSearchRepository bulletinPaieSearchRepository;

  @Autowired
  private MockMvc restBulletinPaieMockMvc;

  private BulletinPaie bulletinPaie;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static BulletinPaie createEntity() {
    BulletinPaie bulletinPaie = new BulletinPaie().refBulletin(DEFAULT_REF_BULLETIN);
    return bulletinPaie;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static BulletinPaie createUpdatedEntity() {
    BulletinPaie bulletinPaie = new BulletinPaie().refBulletin(UPDATED_REF_BULLETIN);
    return bulletinPaie;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    bulletinPaieSearchRepository.deleteAll();
    assertThat(bulletinPaieSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    bulletinPaieRepository.deleteAll();
    bulletinPaie = createEntity();
  }

  @Test
  void createBulletinPaie() throws Exception {
    int databaseSizeBeforeCreate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);
    restBulletinPaieMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO)))
      .andExpect(status().isCreated());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    BulletinPaie testBulletinPaie = bulletinPaieList.get(bulletinPaieList.size() - 1);
    assertThat(testBulletinPaie.getRefBulletin()).isEqualTo(DEFAULT_REF_BULLETIN);
  }

  @Test
  void createBulletinPaieWithExistingId() throws Exception {
    // Create the BulletinPaie with an existing ID
    bulletinPaie.setId("existing_id");
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    int databaseSizeBeforeCreate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restBulletinPaieMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO)))
      .andExpect(status().isBadRequest());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllBulletinPaies() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);

    // Get all the bulletinPaieList
    restBulletinPaieMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(bulletinPaie.getId())))
      .andExpect(jsonPath("$.[*].refBulletin").value(hasItem(DEFAULT_REF_BULLETIN)));
  }

  @Test
  void getBulletinPaie() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);

    // Get the bulletinPaie
    restBulletinPaieMockMvc
      .perform(get(ENTITY_API_URL_ID, bulletinPaie.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(bulletinPaie.getId()))
      .andExpect(jsonPath("$.refBulletin").value(DEFAULT_REF_BULLETIN));
  }

  @Test
  void getNonExistingBulletinPaie() throws Exception {
    // Get the bulletinPaie
    restBulletinPaieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingBulletinPaie() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);

    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    bulletinPaieSearchRepository.save(bulletinPaie);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());

    // Update the bulletinPaie
    BulletinPaie updatedBulletinPaie = bulletinPaieRepository.findById(bulletinPaie.getId()).get();
    updatedBulletinPaie.refBulletin(UPDATED_REF_BULLETIN);
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(updatedBulletinPaie);

    restBulletinPaieMockMvc
      .perform(
        put(ENTITY_API_URL_ID, bulletinPaieDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isOk());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    BulletinPaie testBulletinPaie = bulletinPaieList.get(bulletinPaieList.size() - 1);
    assertThat(testBulletinPaie.getRefBulletin()).isEqualTo(UPDATED_REF_BULLETIN);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<BulletinPaie> bulletinPaieSearchList = IterableUtils.toList(bulletinPaieSearchRepository.findAll());
        BulletinPaie testBulletinPaieSearch = bulletinPaieSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testBulletinPaieSearch.getRefBulletin()).isEqualTo(UPDATED_REF_BULLETIN);
      });
  }

  @Test
  void putNonExistingBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(
        put(ENTITY_API_URL_ID, bulletinPaieDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateBulletinPaieWithPatch() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);

    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();

    // Update the bulletinPaie using partial update
    BulletinPaie partialUpdatedBulletinPaie = new BulletinPaie();
    partialUpdatedBulletinPaie.setId(bulletinPaie.getId());

    restBulletinPaieMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedBulletinPaie.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBulletinPaie))
      )
      .andExpect(status().isOk());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    BulletinPaie testBulletinPaie = bulletinPaieList.get(bulletinPaieList.size() - 1);
    assertThat(testBulletinPaie.getRefBulletin()).isEqualTo(DEFAULT_REF_BULLETIN);
  }

  @Test
  void fullUpdateBulletinPaieWithPatch() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);

    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();

    // Update the bulletinPaie using partial update
    BulletinPaie partialUpdatedBulletinPaie = new BulletinPaie();
    partialUpdatedBulletinPaie.setId(bulletinPaie.getId());

    partialUpdatedBulletinPaie.refBulletin(UPDATED_REF_BULLETIN);

    restBulletinPaieMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedBulletinPaie.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBulletinPaie))
      )
      .andExpect(status().isOk());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    BulletinPaie testBulletinPaie = bulletinPaieList.get(bulletinPaieList.size() - 1);
    assertThat(testBulletinPaie.getRefBulletin()).isEqualTo(UPDATED_REF_BULLETIN);
  }

  @Test
  void patchNonExistingBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, bulletinPaieDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamBulletinPaie() throws Exception {
    int databaseSizeBeforeUpdate = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    bulletinPaie.setId(UUID.randomUUID().toString());

    // Create the BulletinPaie
    BulletinPaieDTO bulletinPaieDTO = bulletinPaieMapper.toDto(bulletinPaie);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restBulletinPaieMockMvc
      .perform(
        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bulletinPaieDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the BulletinPaie in the database
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteBulletinPaie() throws Exception {
    // Initialize the database
    bulletinPaieRepository.save(bulletinPaie);
    bulletinPaieRepository.save(bulletinPaie);
    bulletinPaieSearchRepository.save(bulletinPaie);

    int databaseSizeBeforeDelete = bulletinPaieRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the bulletinPaie
    restBulletinPaieMockMvc
      .perform(delete(ENTITY_API_URL_ID, bulletinPaie.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<BulletinPaie> bulletinPaieList = bulletinPaieRepository.findAll();
    assertThat(bulletinPaieList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(bulletinPaieSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchBulletinPaie() throws Exception {
    // Initialize the database
    bulletinPaie = bulletinPaieRepository.save(bulletinPaie);
    bulletinPaieSearchRepository.save(bulletinPaie);

    // Search the bulletinPaie
    restBulletinPaieMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + bulletinPaie.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(bulletinPaie.getId())))
      .andExpect(jsonPath("$.[*].refBulletin").value(hasItem(DEFAULT_REF_BULLETIN)));
  }
}
