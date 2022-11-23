package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Traitement;
import com.brain.fisc.domain.enumeration.TypeTraitement;
import com.brain.fisc.repository.TraitementRepository;
import com.brain.fisc.repository.search.TraitementSearchRepository;
import com.brain.fisc.service.dto.TraitementDTO;
import com.brain.fisc.service.mapper.TraitementMapper;
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
 * Integration tests for the {@link TraitementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TraitementResourceIT {

  private static final String DEFAULT_REF_TRAITEMENT = "AAAAAAAAAA";
  private static final String UPDATED_REF_TRAITEMENT = "BBBBBBBBBB";

  private static final TypeTraitement DEFAULT_TYPE_TRAITEMENT = TypeTraitement.AVANCE;
  private static final TypeTraitement UPDATED_TYPE_TRAITEMENT = TypeTraitement.OPPOSITION;

  private static final Double DEFAULT_MONTANT = 1D;
  private static final Double UPDATED_MONTANT = 2D;

  private static final String ENTITY_API_URL = "/api/traitements";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/traitements";

  @Autowired
  private TraitementRepository traitementRepository;

  @Autowired
  private TraitementMapper traitementMapper;

  @Autowired
  private TraitementSearchRepository traitementSearchRepository;

  @Autowired
  private MockMvc restTraitementMockMvc;

  private Traitement traitement;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Traitement createEntity() {
    Traitement traitement = new Traitement()
      .refTraitement(DEFAULT_REF_TRAITEMENT)
      .typeTraitement(DEFAULT_TYPE_TRAITEMENT)
      .montant(DEFAULT_MONTANT);
    return traitement;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Traitement createUpdatedEntity() {
    Traitement traitement = new Traitement()
      .refTraitement(UPDATED_REF_TRAITEMENT)
      .typeTraitement(UPDATED_TYPE_TRAITEMENT)
      .montant(UPDATED_MONTANT);
    return traitement;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    traitementSearchRepository.deleteAll();
    assertThat(traitementSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    traitementRepository.deleteAll();
    traitement = createEntity();
  }

  @Test
  void createTraitement() throws Exception {
    int databaseSizeBeforeCreate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);
    restTraitementMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(traitementDTO)))
      .andExpect(status().isCreated());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Traitement testTraitement = traitementList.get(traitementList.size() - 1);
    assertThat(testTraitement.getRefTraitement()).isEqualTo(DEFAULT_REF_TRAITEMENT);
    assertThat(testTraitement.getTypeTraitement()).isEqualTo(DEFAULT_TYPE_TRAITEMENT);
    assertThat(testTraitement.getMontant()).isEqualTo(DEFAULT_MONTANT);
  }

  @Test
  void createTraitementWithExistingId() throws Exception {
    // Create the Traitement with an existing ID
    traitement.setId("existing_id");
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    int databaseSizeBeforeCreate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restTraitementMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(traitementDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllTraitements() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);

    // Get all the traitementList
    restTraitementMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(traitement.getId())))
      .andExpect(jsonPath("$.[*].refTraitement").value(hasItem(DEFAULT_REF_TRAITEMENT)))
      .andExpect(jsonPath("$.[*].typeTraitement").value(hasItem(DEFAULT_TYPE_TRAITEMENT.toString())))
      .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
  }

  @Test
  void getTraitement() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);

    // Get the traitement
    restTraitementMockMvc
      .perform(get(ENTITY_API_URL_ID, traitement.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(traitement.getId()))
      .andExpect(jsonPath("$.refTraitement").value(DEFAULT_REF_TRAITEMENT))
      .andExpect(jsonPath("$.typeTraitement").value(DEFAULT_TYPE_TRAITEMENT.toString()))
      .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()));
  }

  @Test
  void getNonExistingTraitement() throws Exception {
    // Get the traitement
    restTraitementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingTraitement() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);

    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    traitementSearchRepository.save(traitement);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());

    // Update the traitement
    Traitement updatedTraitement = traitementRepository.findById(traitement.getId()).get();
    updatedTraitement.refTraitement(UPDATED_REF_TRAITEMENT).typeTraitement(UPDATED_TYPE_TRAITEMENT).montant(UPDATED_MONTANT);
    TraitementDTO traitementDTO = traitementMapper.toDto(updatedTraitement);

    restTraitementMockMvc
      .perform(
        put(ENTITY_API_URL_ID, traitementDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(traitementDTO))
      )
      .andExpect(status().isOk());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    Traitement testTraitement = traitementList.get(traitementList.size() - 1);
    assertThat(testTraitement.getRefTraitement()).isEqualTo(UPDATED_REF_TRAITEMENT);
    assertThat(testTraitement.getTypeTraitement()).isEqualTo(UPDATED_TYPE_TRAITEMENT);
    assertThat(testTraitement.getMontant()).isEqualTo(UPDATED_MONTANT);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Traitement> traitementSearchList = IterableUtils.toList(traitementSearchRepository.findAll());
        Traitement testTraitementSearch = traitementSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testTraitementSearch.getRefTraitement()).isEqualTo(UPDATED_REF_TRAITEMENT);
        assertThat(testTraitementSearch.getTypeTraitement()).isEqualTo(UPDATED_TYPE_TRAITEMENT);
        assertThat(testTraitementSearch.getMontant()).isEqualTo(UPDATED_MONTANT);
      });
  }

  @Test
  void putNonExistingTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(
        put(ENTITY_API_URL_ID, traitementDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(traitementDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(traitementDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(traitementDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateTraitementWithPatch() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);

    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();

    // Update the traitement using partial update
    Traitement partialUpdatedTraitement = new Traitement();
    partialUpdatedTraitement.setId(traitement.getId());

    partialUpdatedTraitement.refTraitement(UPDATED_REF_TRAITEMENT).montant(UPDATED_MONTANT);

    restTraitementMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTraitement.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTraitement))
      )
      .andExpect(status().isOk());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    Traitement testTraitement = traitementList.get(traitementList.size() - 1);
    assertThat(testTraitement.getRefTraitement()).isEqualTo(UPDATED_REF_TRAITEMENT);
    assertThat(testTraitement.getTypeTraitement()).isEqualTo(DEFAULT_TYPE_TRAITEMENT);
    assertThat(testTraitement.getMontant()).isEqualTo(UPDATED_MONTANT);
  }

  @Test
  void fullUpdateTraitementWithPatch() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);

    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();

    // Update the traitement using partial update
    Traitement partialUpdatedTraitement = new Traitement();
    partialUpdatedTraitement.setId(traitement.getId());

    partialUpdatedTraitement.refTraitement(UPDATED_REF_TRAITEMENT).typeTraitement(UPDATED_TYPE_TRAITEMENT).montant(UPDATED_MONTANT);

    restTraitementMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTraitement.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTraitement))
      )
      .andExpect(status().isOk());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    Traitement testTraitement = traitementList.get(traitementList.size() - 1);
    assertThat(testTraitement.getRefTraitement()).isEqualTo(UPDATED_REF_TRAITEMENT);
    assertThat(testTraitement.getTypeTraitement()).isEqualTo(UPDATED_TYPE_TRAITEMENT);
    assertThat(testTraitement.getMontant()).isEqualTo(UPDATED_MONTANT);
  }

  @Test
  void patchNonExistingTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, traitementDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(traitementDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(traitementDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamTraitement() throws Exception {
    int databaseSizeBeforeUpdate = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    traitement.setId(UUID.randomUUID().toString());

    // Create the Traitement
    TraitementDTO traitementDTO = traitementMapper.toDto(traitement);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTraitementMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(traitementDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Traitement in the database
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteTraitement() throws Exception {
    // Initialize the database
    traitementRepository.save(traitement);
    traitementRepository.save(traitement);
    traitementSearchRepository.save(traitement);

    int databaseSizeBeforeDelete = traitementRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the traitement
    restTraitementMockMvc
      .perform(delete(ENTITY_API_URL_ID, traitement.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Traitement> traitementList = traitementRepository.findAll();
    assertThat(traitementList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(traitementSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchTraitement() throws Exception {
    // Initialize the database
    traitement = traitementRepository.save(traitement);
    traitementSearchRepository.save(traitement);

    // Search the traitement
    restTraitementMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + traitement.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(traitement.getId())))
      .andExpect(jsonPath("$.[*].refTraitement").value(hasItem(DEFAULT_REF_TRAITEMENT)))
      .andExpect(jsonPath("$.[*].typeTraitement").value(hasItem(DEFAULT_TYPE_TRAITEMENT.toString())))
      .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
  }
}
