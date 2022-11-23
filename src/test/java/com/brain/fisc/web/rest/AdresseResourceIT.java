package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Adresse;
import com.brain.fisc.repository.AdresseRepository;
import com.brain.fisc.repository.search.AdresseSearchRepository;
import com.brain.fisc.service.dto.AdresseDTO;
import com.brain.fisc.service.mapper.AdresseMapper;
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
 * Integration tests for the {@link AdresseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdresseResourceIT {

  private static final String DEFAULT_CEL = "AAAAAAAA";
  private static final String UPDATED_CEL = "BBBBBBBB";

  private static final String DEFAULT_TEL = "AAAAAAAA";
  private static final String UPDATED_TEL = "BBBBBBBB";

  private static final String DEFAULT_REGION = "AAAAAAAAAA";
  private static final String UPDATED_REGION = "BBBBBBBBBB";

  private static final String DEFAULT_NOM_RUE = "AAAAAAAAAA";
  private static final String UPDATED_NOM_RUE = "BBBBBBBBBB";

  private static final String DEFAULT_NUM_RUE = "AAAAAAAAAA";
  private static final String UPDATED_NUM_RUE = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/adresses";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/adresses";

  @Autowired
  private AdresseRepository adresseRepository;

  @Autowired
  private AdresseMapper adresseMapper;

  @Autowired
  private AdresseSearchRepository adresseSearchRepository;

  @Autowired
  private MockMvc restAdresseMockMvc;

  private Adresse adresse;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Adresse createEntity() {
    Adresse adresse = new Adresse()
      .cel(DEFAULT_CEL)
      .tel(DEFAULT_TEL)
      .region(DEFAULT_REGION)
      .nomRue(DEFAULT_NOM_RUE)
      .numRue(DEFAULT_NUM_RUE);
    return adresse;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Adresse createUpdatedEntity() {
    Adresse adresse = new Adresse()
      .cel(UPDATED_CEL)
      .tel(UPDATED_TEL)
      .region(UPDATED_REGION)
      .nomRue(UPDATED_NOM_RUE)
      .numRue(UPDATED_NUM_RUE);
    return adresse;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    adresseSearchRepository.deleteAll();
    assertThat(adresseSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    adresseRepository.deleteAll();
    adresse = createEntity();
  }

  @Test
  void createAdresse() throws Exception {
    int databaseSizeBeforeCreate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);
    restAdresseMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adresseDTO)))
      .andExpect(status().isCreated());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Adresse testAdresse = adresseList.get(adresseList.size() - 1);
    assertThat(testAdresse.getCel()).isEqualTo(DEFAULT_CEL);
    assertThat(testAdresse.getTel()).isEqualTo(DEFAULT_TEL);
    assertThat(testAdresse.getRegion()).isEqualTo(DEFAULT_REGION);
    assertThat(testAdresse.getNomRue()).isEqualTo(DEFAULT_NOM_RUE);
    assertThat(testAdresse.getNumRue()).isEqualTo(DEFAULT_NUM_RUE);
  }

  @Test
  void createAdresseWithExistingId() throws Exception {
    // Create the Adresse with an existing ID
    adresse.setId("existing_id");
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    int databaseSizeBeforeCreate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restAdresseMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adresseDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllAdresses() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);

    // Get all the adresseList
    restAdresseMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(adresse.getId())))
      .andExpect(jsonPath("$.[*].cel").value(hasItem(DEFAULT_CEL)))
      .andExpect(jsonPath("$.[*].tel").value(hasItem(DEFAULT_TEL)))
      .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
      .andExpect(jsonPath("$.[*].nomRue").value(hasItem(DEFAULT_NOM_RUE)))
      .andExpect(jsonPath("$.[*].numRue").value(hasItem(DEFAULT_NUM_RUE)));
  }

  @Test
  void getAdresse() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);

    // Get the adresse
    restAdresseMockMvc
      .perform(get(ENTITY_API_URL_ID, adresse.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(adresse.getId()))
      .andExpect(jsonPath("$.cel").value(DEFAULT_CEL))
      .andExpect(jsonPath("$.tel").value(DEFAULT_TEL))
      .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
      .andExpect(jsonPath("$.nomRue").value(DEFAULT_NOM_RUE))
      .andExpect(jsonPath("$.numRue").value(DEFAULT_NUM_RUE));
  }

  @Test
  void getNonExistingAdresse() throws Exception {
    // Get the adresse
    restAdresseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingAdresse() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);

    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    adresseSearchRepository.save(adresse);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());

    // Update the adresse
    Adresse updatedAdresse = adresseRepository.findById(adresse.getId()).get();
    updatedAdresse.cel(UPDATED_CEL).tel(UPDATED_TEL).region(UPDATED_REGION).nomRue(UPDATED_NOM_RUE).numRue(UPDATED_NUM_RUE);
    AdresseDTO adresseDTO = adresseMapper.toDto(updatedAdresse);

    restAdresseMockMvc
      .perform(
        put(ENTITY_API_URL_ID, adresseDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(adresseDTO))
      )
      .andExpect(status().isOk());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    Adresse testAdresse = adresseList.get(adresseList.size() - 1);
    assertThat(testAdresse.getCel()).isEqualTo(UPDATED_CEL);
    assertThat(testAdresse.getTel()).isEqualTo(UPDATED_TEL);
    assertThat(testAdresse.getRegion()).isEqualTo(UPDATED_REGION);
    assertThat(testAdresse.getNomRue()).isEqualTo(UPDATED_NOM_RUE);
    assertThat(testAdresse.getNumRue()).isEqualTo(UPDATED_NUM_RUE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Adresse> adresseSearchList = IterableUtils.toList(adresseSearchRepository.findAll());
        Adresse testAdresseSearch = adresseSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testAdresseSearch.getCel()).isEqualTo(UPDATED_CEL);
        assertThat(testAdresseSearch.getTel()).isEqualTo(UPDATED_TEL);
        assertThat(testAdresseSearch.getRegion()).isEqualTo(UPDATED_REGION);
        assertThat(testAdresseSearch.getNomRue()).isEqualTo(UPDATED_NOM_RUE);
        assertThat(testAdresseSearch.getNumRue()).isEqualTo(UPDATED_NUM_RUE);
      });
  }

  @Test
  void putNonExistingAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(
        put(ENTITY_API_URL_ID, adresseDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(adresseDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(adresseDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adresseDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdateAdresseWithPatch() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);

    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();

    // Update the adresse using partial update
    Adresse partialUpdatedAdresse = new Adresse();
    partialUpdatedAdresse.setId(adresse.getId());

    partialUpdatedAdresse.cel(UPDATED_CEL).tel(UPDATED_TEL).region(UPDATED_REGION).nomRue(UPDATED_NOM_RUE);

    restAdresseMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedAdresse.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdresse))
      )
      .andExpect(status().isOk());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    Adresse testAdresse = adresseList.get(adresseList.size() - 1);
    assertThat(testAdresse.getCel()).isEqualTo(UPDATED_CEL);
    assertThat(testAdresse.getTel()).isEqualTo(UPDATED_TEL);
    assertThat(testAdresse.getRegion()).isEqualTo(UPDATED_REGION);
    assertThat(testAdresse.getNomRue()).isEqualTo(UPDATED_NOM_RUE);
    assertThat(testAdresse.getNumRue()).isEqualTo(DEFAULT_NUM_RUE);
  }

  @Test
  void fullUpdateAdresseWithPatch() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);

    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();

    // Update the adresse using partial update
    Adresse partialUpdatedAdresse = new Adresse();
    partialUpdatedAdresse.setId(adresse.getId());

    partialUpdatedAdresse.cel(UPDATED_CEL).tel(UPDATED_TEL).region(UPDATED_REGION).nomRue(UPDATED_NOM_RUE).numRue(UPDATED_NUM_RUE);

    restAdresseMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedAdresse.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdresse))
      )
      .andExpect(status().isOk());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    Adresse testAdresse = adresseList.get(adresseList.size() - 1);
    assertThat(testAdresse.getCel()).isEqualTo(UPDATED_CEL);
    assertThat(testAdresse.getTel()).isEqualTo(UPDATED_TEL);
    assertThat(testAdresse.getRegion()).isEqualTo(UPDATED_REGION);
    assertThat(testAdresse.getNomRue()).isEqualTo(UPDATED_NOM_RUE);
    assertThat(testAdresse.getNumRue()).isEqualTo(UPDATED_NUM_RUE);
  }

  @Test
  void patchNonExistingAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, adresseDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(adresseDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(adresseDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamAdresse() throws Exception {
    int databaseSizeBeforeUpdate = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    adresse.setId(UUID.randomUUID().toString());

    // Create the Adresse
    AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restAdresseMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(adresseDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Adresse in the database
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deleteAdresse() throws Exception {
    // Initialize the database
    adresseRepository.save(adresse);
    adresseRepository.save(adresse);
    adresseSearchRepository.save(adresse);

    int databaseSizeBeforeDelete = adresseRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the adresse
    restAdresseMockMvc
      .perform(delete(ENTITY_API_URL_ID, adresse.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Adresse> adresseList = adresseRepository.findAll();
    assertThat(adresseList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(adresseSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchAdresse() throws Exception {
    // Initialize the database
    adresse = adresseRepository.save(adresse);
    adresseSearchRepository.save(adresse);

    // Search the adresse
    restAdresseMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + adresse.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(adresse.getId())))
      .andExpect(jsonPath("$.[*].cel").value(hasItem(DEFAULT_CEL)))
      .andExpect(jsonPath("$.[*].tel").value(hasItem(DEFAULT_TEL)))
      .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
      .andExpect(jsonPath("$.[*].nomRue").value(hasItem(DEFAULT_NOM_RUE)))
      .andExpect(jsonPath("$.[*].numRue").value(hasItem(DEFAULT_NUM_RUE)));
  }
}
