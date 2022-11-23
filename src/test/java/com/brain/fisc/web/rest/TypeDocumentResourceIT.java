package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.TypeDocument;
import com.brain.fisc.repository.TypeDocumentRepository;
import com.brain.fisc.repository.search.TypeDocumentSearchRepository;
import com.brain.fisc.service.dto.TypeDocumentDTO;
import com.brain.fisc.service.mapper.TypeDocumentMapper;
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
 * Integration tests for the {@link TypeDocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeDocumentResourceIT {

  private static final String DEFAULT_INT_TYPE_DOC = "AAAAAAAAAA";
  private static final String UPDATED_INT_TYPE_DOC = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/type-documents";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/type-documents";

  @Autowired
  private TypeDocumentRepository typeDocumentRepository;

  @Autowired
  private TypeDocumentMapper typeDocumentMapper;

  @Autowired
  private TypeDocumentSearchRepository typeDocumentSearchRepository;

  @Autowired
  private MockMvc restTypeDocumentMockMvc;

  private TypeDocument typeDocument;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static TypeDocument createEntity() {
    TypeDocument typeDocument = new TypeDocument().intTypeDoc(DEFAULT_INT_TYPE_DOC);
    return typeDocument;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static TypeDocument createUpdatedEntity() {
    TypeDocument typeDocument = new TypeDocument().intTypeDoc(UPDATED_INT_TYPE_DOC);
    return typeDocument;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    typeDocumentSearchRepository.deleteAll();
    assertThat(typeDocumentSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    typeDocumentRepository.deleteAll();
    typeDocument = createEntity();
  }

  @Test
  void createTypeDocument() throws Exception {
    int databaseSizeBeforeCreate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);
    restTypeDocumentMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO)))
      .andExpect(status().isCreated());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    TypeDocument testTypeDocument = typeDocumentList.get(typeDocumentList.size() - 1);
    assertThat(testTypeDocument.getIntTypeDoc()).isEqualTo(DEFAULT_INT_TYPE_DOC);
  }

  @Test
  void createTypeDocumentWithExistingId() throws Exception {
    // Create the TypeDocument with an existing ID
    typeDocument.setId("existing_id");
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    int databaseSizeBeforeCreate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restTypeDocumentMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO)))
      .andExpect(status().isBadRequest());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllTypeDocuments() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);

    // Get all the typeDocumentList
    restTypeDocumentMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(typeDocument.getId())))
      .andExpect(jsonPath("$.[*].intTypeDoc").value(hasItem(DEFAULT_INT_TYPE_DOC)));
  }

  @Test
  void getTypeDocument() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);

    // Get the typeDocument
    restTypeDocumentMockMvc
      .perform(get(ENTITY_API_URL_ID, typeDocument.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(typeDocument.getId()))
      .andExpect(jsonPath("$.intTypeDoc").value(DEFAULT_INT_TYPE_DOC));
  }

  @Test
  void getNonExistingTypeDocument() throws Exception {
    // Get the typeDocument
    restTypeDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingTypeDocument() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);

    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    typeDocumentSearchRepository.save(typeDocument);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());

    // Update the typeDocument
    TypeDocument updatedTypeDocument = typeDocumentRepository.findById(typeDocument.getId()).get();
    updatedTypeDocument.intTypeDoc(UPDATED_INT_TYPE_DOC);
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(updatedTypeDocument);

    restTypeDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, typeDocumentDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isOk());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    TypeDocument testTypeDocument = typeDocumentList.get(typeDocumentList.size() - 1);
    assertThat(testTypeDocument.getIntTypeDoc()).isEqualTo(UPDATED_INT_TYPE_DOC);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<TypeDocument> typeDocumentSearchList = IterableUtils.toList(typeDocumentSearchRepository.findAll());
        TypeDocument testTypeDocumentSearch = typeDocumentSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testTypeDocumentSearch.getIntTypeDoc()).isEqualTo(UPDATED_INT_TYPE_DOC);
      });
  }

  @Test
  void putNonExistingTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, typeDocumentDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateTypeDocumentWithPatch() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);

    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();

    // Update the typeDocument using partial update
    TypeDocument partialUpdatedTypeDocument = new TypeDocument();
    partialUpdatedTypeDocument.setId(typeDocument.getId());

    partialUpdatedTypeDocument.intTypeDoc(UPDATED_INT_TYPE_DOC);

    restTypeDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTypeDocument.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeDocument))
      )
      .andExpect(status().isOk());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    TypeDocument testTypeDocument = typeDocumentList.get(typeDocumentList.size() - 1);
    assertThat(testTypeDocument.getIntTypeDoc()).isEqualTo(UPDATED_INT_TYPE_DOC);
  }

  @Test
  void fullUpdateTypeDocumentWithPatch() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);

    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();

    // Update the typeDocument using partial update
    TypeDocument partialUpdatedTypeDocument = new TypeDocument();
    partialUpdatedTypeDocument.setId(typeDocument.getId());

    partialUpdatedTypeDocument.intTypeDoc(UPDATED_INT_TYPE_DOC);

    restTypeDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedTypeDocument.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeDocument))
      )
      .andExpect(status().isOk());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    TypeDocument testTypeDocument = typeDocumentList.get(typeDocumentList.size() - 1);
    assertThat(testTypeDocument.getIntTypeDoc()).isEqualTo(UPDATED_INT_TYPE_DOC);
  }

  @Test
  void patchNonExistingTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, typeDocumentDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamTypeDocument() throws Exception {
    int databaseSizeBeforeUpdate = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    typeDocument.setId(UUID.randomUUID().toString());

    // Create the TypeDocument
    TypeDocumentDTO typeDocumentDTO = typeDocumentMapper.toDto(typeDocument);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restTypeDocumentMockMvc
      .perform(
        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(typeDocumentDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the TypeDocument in the database
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteTypeDocument() throws Exception {
    // Initialize the database
    typeDocumentRepository.save(typeDocument);
    typeDocumentRepository.save(typeDocument);
    typeDocumentSearchRepository.save(typeDocument);

    int databaseSizeBeforeDelete = typeDocumentRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the typeDocument
    restTypeDocumentMockMvc
      .perform(delete(ENTITY_API_URL_ID, typeDocument.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<TypeDocument> typeDocumentList = typeDocumentRepository.findAll();
    assertThat(typeDocumentList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(typeDocumentSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchTypeDocument() throws Exception {
    // Initialize the database
    typeDocument = typeDocumentRepository.save(typeDocument);
    typeDocumentSearchRepository.save(typeDocument);

    // Search the typeDocument
    restTypeDocumentMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + typeDocument.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(typeDocument.getId())))
      .andExpect(jsonPath("$.[*].intTypeDoc").value(hasItem(DEFAULT_INT_TYPE_DOC)));
  }
}
