package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.ComposantDocument;
import com.brain.fisc.repository.ComposantDocumentRepository;
import com.brain.fisc.repository.search.ComposantDocumentSearchRepository;
import com.brain.fisc.service.dto.ComposantDocumentDTO;
import com.brain.fisc.service.mapper.ComposantDocumentMapper;
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
 * Integration tests for the {@link ComposantDocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComposantDocumentResourceIT {

  private static final String DEFAULT_INT_COMPOSANT = "AAAAAAAAAA";
  private static final String UPDATED_INT_COMPOSANT = "BBBBBBBBBB";

  private static final String DEFAULT_TITRE_COMPOSANT = "AAAAAAAAAA";
  private static final String UPDATED_TITRE_COMPOSANT = "BBBBBBBBBB";

  private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
  private static final String UPDATED_CONTENU = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/composant-documents";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/composant-documents";

  @Autowired
  private ComposantDocumentRepository composantDocumentRepository;

  @Autowired
  private ComposantDocumentMapper composantDocumentMapper;

  @Autowired
  private ComposantDocumentSearchRepository composantDocumentSearchRepository;

  @Autowired
  private MockMvc restComposantDocumentMockMvc;

  private ComposantDocument composantDocument;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static ComposantDocument createEntity() {
    ComposantDocument composantDocument = new ComposantDocument()
      .intComposant(DEFAULT_INT_COMPOSANT)
      .titreComposant(DEFAULT_TITRE_COMPOSANT)
      .contenu(DEFAULT_CONTENU);
    return composantDocument;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static ComposantDocument createUpdatedEntity() {
    ComposantDocument composantDocument = new ComposantDocument()
      .intComposant(UPDATED_INT_COMPOSANT)
      .titreComposant(UPDATED_TITRE_COMPOSANT)
      .contenu(UPDATED_CONTENU);
    return composantDocument;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    composantDocumentSearchRepository.deleteAll();
    assertThat(composantDocumentSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    composantDocumentRepository.deleteAll();
    composantDocument = createEntity();
  }

  @Test
  void createComposantDocument() throws Exception {
    int databaseSizeBeforeCreate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);
    restComposantDocumentMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isCreated());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    ComposantDocument testComposantDocument = composantDocumentList.get(composantDocumentList.size() - 1);
    assertThat(testComposantDocument.getIntComposant()).isEqualTo(DEFAULT_INT_COMPOSANT);
    assertThat(testComposantDocument.getTitreComposant()).isEqualTo(DEFAULT_TITRE_COMPOSANT);
    assertThat(testComposantDocument.getContenu()).isEqualTo(DEFAULT_CONTENU);
  }

  @Test
  void createComposantDocumentWithExistingId() throws Exception {
    // Create the ComposantDocument with an existing ID
    composantDocument.setId("existing_id");
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    int databaseSizeBeforeCreate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restComposantDocumentMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkIntComposantIsRequired() throws Exception {
    int databaseSizeBeforeTest = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    // set the field null
    composantDocument.setIntComposant(null);

    // Create the ComposantDocument, which fails.
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    restComposantDocumentMockMvc
      .perform(
        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllComposantDocuments() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);

    // Get all the composantDocumentList
    restComposantDocumentMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(composantDocument.getId())))
      .andExpect(jsonPath("$.[*].intComposant").value(hasItem(DEFAULT_INT_COMPOSANT)))
      .andExpect(jsonPath("$.[*].titreComposant").value(hasItem(DEFAULT_TITRE_COMPOSANT)))
      .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)));
  }

  @Test
  void getComposantDocument() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);

    // Get the composantDocument
    restComposantDocumentMockMvc
      .perform(get(ENTITY_API_URL_ID, composantDocument.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(composantDocument.getId()))
      .andExpect(jsonPath("$.intComposant").value(DEFAULT_INT_COMPOSANT))
      .andExpect(jsonPath("$.titreComposant").value(DEFAULT_TITRE_COMPOSANT))
      .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU));
  }

  @Test
  void getNonExistingComposantDocument() throws Exception {
    // Get the composantDocument
    restComposantDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingComposantDocument() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);

    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    composantDocumentSearchRepository.save(composantDocument);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());

    // Update the composantDocument
    ComposantDocument updatedComposantDocument = composantDocumentRepository.findById(composantDocument.getId()).get();
    updatedComposantDocument.intComposant(UPDATED_INT_COMPOSANT).titreComposant(UPDATED_TITRE_COMPOSANT).contenu(UPDATED_CONTENU);
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(updatedComposantDocument);

    restComposantDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, composantDocumentDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isOk());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    ComposantDocument testComposantDocument = composantDocumentList.get(composantDocumentList.size() - 1);
    assertThat(testComposantDocument.getIntComposant()).isEqualTo(UPDATED_INT_COMPOSANT);
    assertThat(testComposantDocument.getTitreComposant()).isEqualTo(UPDATED_TITRE_COMPOSANT);
    assertThat(testComposantDocument.getContenu()).isEqualTo(UPDATED_CONTENU);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<ComposantDocument> composantDocumentSearchList = IterableUtils.toList(composantDocumentSearchRepository.findAll());
        ComposantDocument testComposantDocumentSearch = composantDocumentSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testComposantDocumentSearch.getIntComposant()).isEqualTo(UPDATED_INT_COMPOSANT);
        assertThat(testComposantDocumentSearch.getTitreComposant()).isEqualTo(UPDATED_TITRE_COMPOSANT);
        assertThat(testComposantDocumentSearch.getContenu()).isEqualTo(UPDATED_CONTENU);
      });
  }

  @Test
  void putNonExistingComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, composantDocumentDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateComposantDocumentWithPatch() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);

    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();

    // Update the composantDocument using partial update
    ComposantDocument partialUpdatedComposantDocument = new ComposantDocument();
    partialUpdatedComposantDocument.setId(composantDocument.getId());

    restComposantDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedComposantDocument.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComposantDocument))
      )
      .andExpect(status().isOk());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    ComposantDocument testComposantDocument = composantDocumentList.get(composantDocumentList.size() - 1);
    assertThat(testComposantDocument.getIntComposant()).isEqualTo(DEFAULT_INT_COMPOSANT);
    assertThat(testComposantDocument.getTitreComposant()).isEqualTo(DEFAULT_TITRE_COMPOSANT);
    assertThat(testComposantDocument.getContenu()).isEqualTo(DEFAULT_CONTENU);
  }

  @Test
  void fullUpdateComposantDocumentWithPatch() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);

    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();

    // Update the composantDocument using partial update
    ComposantDocument partialUpdatedComposantDocument = new ComposantDocument();
    partialUpdatedComposantDocument.setId(composantDocument.getId());

    partialUpdatedComposantDocument.intComposant(UPDATED_INT_COMPOSANT).titreComposant(UPDATED_TITRE_COMPOSANT).contenu(UPDATED_CONTENU);

    restComposantDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedComposantDocument.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComposantDocument))
      )
      .andExpect(status().isOk());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    ComposantDocument testComposantDocument = composantDocumentList.get(composantDocumentList.size() - 1);
    assertThat(testComposantDocument.getIntComposant()).isEqualTo(UPDATED_INT_COMPOSANT);
    assertThat(testComposantDocument.getTitreComposant()).isEqualTo(UPDATED_TITRE_COMPOSANT);
    assertThat(testComposantDocument.getContenu()).isEqualTo(UPDATED_CONTENU);
  }

  @Test
  void patchNonExistingComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, composantDocumentDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamComposantDocument() throws Exception {
    int databaseSizeBeforeUpdate = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    composantDocument.setId(UUID.randomUUID().toString());

    // Create the ComposantDocument
    ComposantDocumentDTO composantDocumentDTO = composantDocumentMapper.toDto(composantDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restComposantDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(composantDocumentDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the ComposantDocument in the database
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteComposantDocument() throws Exception {
    // Initialize the database
    composantDocumentRepository.save(composantDocument);
    composantDocumentRepository.save(composantDocument);
    composantDocumentSearchRepository.save(composantDocument);

    int databaseSizeBeforeDelete = composantDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the composantDocument
    restComposantDocumentMockMvc
      .perform(delete(ENTITY_API_URL_ID, composantDocument.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<ComposantDocument> composantDocumentList = composantDocumentRepository.findAll();
    assertThat(composantDocumentList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(composantDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchComposantDocument() throws Exception {
    // Initialize the database
    composantDocument = composantDocumentRepository.save(composantDocument);
    composantDocumentSearchRepository.save(composantDocument);

    // Search the composantDocument
    restComposantDocumentMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + composantDocument.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(composantDocument.getId())))
      .andExpect(jsonPath("$.[*].intComposant").value(hasItem(DEFAULT_INT_COMPOSANT)))
      .andExpect(jsonPath("$.[*].titreComposant").value(hasItem(DEFAULT_TITRE_COMPOSANT)))
      .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)));
  }
}
