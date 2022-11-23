package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.DateOperation;
import com.brain.fisc.repository.DateOperationRepository;
import com.brain.fisc.repository.search.DateOperationSearchRepository;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.service.mapper.DateOperationMapper;
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
 * Integration tests for the {@link DateOperationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DateOperationResourceIT {

  private static final LocalDate DEFAULT_DATOPERATION = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_DATOPERATION = LocalDate.now(ZoneId.systemDefault());

  private static final String ENTITY_API_URL = "/api/date-operations";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/date-operations";

  @Autowired
  private DateOperationRepository dateOperationRepository;

  @Autowired
  private DateOperationMapper dateOperationMapper;

  @Autowired
  private DateOperationSearchRepository dateOperationSearchRepository;

  @Autowired
  private MockMvc restDateOperationMockMvc;

  private DateOperation dateOperation;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static DateOperation createEntity() {
    DateOperation dateOperation = new DateOperation().datoperation(DEFAULT_DATOPERATION);
    return dateOperation;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static DateOperation createUpdatedEntity() {
    DateOperation dateOperation = new DateOperation().datoperation(UPDATED_DATOPERATION);
    return dateOperation;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    dateOperationSearchRepository.deleteAll();
    assertThat(dateOperationSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    dateOperationRepository.deleteAll();
    dateOperation = createEntity();
  }

  @Test
  void createDateOperation() throws Exception {
    int databaseSizeBeforeCreate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);
    restDateOperationMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dateOperationDTO)))
      .andExpect(status().isCreated());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    DateOperation testDateOperation = dateOperationList.get(dateOperationList.size() - 1);
    assertThat(testDateOperation.getDatoperation()).isEqualTo(DEFAULT_DATOPERATION);
  }

  @Test
  void createDateOperationWithExistingId() throws Exception {
    // Create the DateOperation with an existing ID
    dateOperation.setId("existing_id");
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    int databaseSizeBeforeCreate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restDateOperationMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dateOperationDTO)))
      .andExpect(status().isBadRequest());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllDateOperations() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);

    // Get all the dateOperationList
    restDateOperationMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(dateOperation.getId())))
      .andExpect(jsonPath("$.[*].datoperation").value(hasItem(DEFAULT_DATOPERATION.toString())));
  }

  @Test
  void getDateOperation() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);

    // Get the dateOperation
    restDateOperationMockMvc
      .perform(get(ENTITY_API_URL_ID, dateOperation.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(dateOperation.getId()))
      .andExpect(jsonPath("$.datoperation").value(DEFAULT_DATOPERATION.toString()));
  }

  @Test
  void getNonExistingDateOperation() throws Exception {
    // Get the dateOperation
    restDateOperationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingDateOperation() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);

    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    dateOperationSearchRepository.save(dateOperation);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());

    // Update the dateOperation
    DateOperation updatedDateOperation = dateOperationRepository.findById(dateOperation.getId()).get();
    updatedDateOperation.datoperation(UPDATED_DATOPERATION);
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(updatedDateOperation);

    restDateOperationMockMvc
      .perform(
        put(ENTITY_API_URL_ID, dateOperationDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isOk());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    DateOperation testDateOperation = dateOperationList.get(dateOperationList.size() - 1);
    assertThat(testDateOperation.getDatoperation()).isEqualTo(UPDATED_DATOPERATION);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<DateOperation> dateOperationSearchList = IterableUtils.toList(dateOperationSearchRepository.findAll());
        DateOperation testDateOperationSearch = dateOperationSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testDateOperationSearch.getDatoperation()).isEqualTo(UPDATED_DATOPERATION);
      });
  }

  @Test
  void putNonExistingDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(
        put(ENTITY_API_URL_ID, dateOperationDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dateOperationDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateDateOperationWithPatch() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);

    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();

    // Update the dateOperation using partial update
    DateOperation partialUpdatedDateOperation = new DateOperation();
    partialUpdatedDateOperation.setId(dateOperation.getId());

    restDateOperationMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedDateOperation.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDateOperation))
      )
      .andExpect(status().isOk());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    DateOperation testDateOperation = dateOperationList.get(dateOperationList.size() - 1);
    assertThat(testDateOperation.getDatoperation()).isEqualTo(DEFAULT_DATOPERATION);
  }

  @Test
  void fullUpdateDateOperationWithPatch() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);

    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();

    // Update the dateOperation using partial update
    DateOperation partialUpdatedDateOperation = new DateOperation();
    partialUpdatedDateOperation.setId(dateOperation.getId());

    partialUpdatedDateOperation.datoperation(UPDATED_DATOPERATION);

    restDateOperationMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedDateOperation.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDateOperation))
      )
      .andExpect(status().isOk());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    DateOperation testDateOperation = dateOperationList.get(dateOperationList.size() - 1);
    assertThat(testDateOperation.getDatoperation()).isEqualTo(UPDATED_DATOPERATION);
  }

  @Test
  void patchNonExistingDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, dateOperationDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamDateOperation() throws Exception {
    int databaseSizeBeforeUpdate = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    dateOperation.setId(UUID.randomUUID().toString());

    // Create the DateOperation
    DateOperationDTO dateOperationDTO = dateOperationMapper.toDto(dateOperation);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restDateOperationMockMvc
      .perform(
        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dateOperationDTO))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the DateOperation in the database
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteDateOperation() throws Exception {
    // Initialize the database
    dateOperationRepository.save(dateOperation);
    dateOperationRepository.save(dateOperation);
    dateOperationSearchRepository.save(dateOperation);

    int databaseSizeBeforeDelete = dateOperationRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the dateOperation
    restDateOperationMockMvc
      .perform(delete(ENTITY_API_URL_ID, dateOperation.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<DateOperation> dateOperationList = dateOperationRepository.findAll();
    assertThat(dateOperationList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(dateOperationSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchDateOperation() throws Exception {
    // Initialize the database
    dateOperation = dateOperationRepository.save(dateOperation);
    dateOperationSearchRepository.save(dateOperation);

    // Search the dateOperation
    restDateOperationMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dateOperation.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(dateOperation.getId())))
      .andExpect(jsonPath("$.[*].datoperation").value(hasItem(DEFAULT_DATOPERATION.toString())));
  }
}
