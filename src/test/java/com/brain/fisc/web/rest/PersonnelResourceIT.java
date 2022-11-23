package com.brain.fisc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.brain.fisc.IntegrationTest;
import com.brain.fisc.domain.Personnel;
import com.brain.fisc.domain.enumeration.Sexe;
import com.brain.fisc.domain.enumeration.SituationMatrimoniale;
import com.brain.fisc.repository.PersonnelRepository;
import com.brain.fisc.repository.search.PersonnelSearchRepository;
import com.brain.fisc.service.dto.PersonnelDTO;
import com.brain.fisc.service.mapper.PersonnelMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PersonnelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonnelResourceIT {

  private static final String DEFAULT_MATRICULE = "AAAAAAAAAA";
  private static final String UPDATED_MATRICULE = "BBBBBBBBBB";

  private static final Sexe DEFAULT_SEXE = Sexe.MASCULIN;
  private static final Sexe UPDATED_SEXE = Sexe.FEMININ;

  private static final String DEFAULT_NOM_ACTEUR = "AAAAAAAAAA";
  private static final String UPDATED_NOM_ACTEUR = "BBBBBBBBBB";

  private static final String DEFAULT_PRENOMS_ACTEUR = "AAAAAAAAAA";
  private static final String UPDATED_PRENOMS_ACTEUR = "BBBBBBBBBB";

  private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
  private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.now(ZoneId.systemDefault());

  private static final String DEFAULT_LIEU_NAISSANCE = "AAAAAAAAAA";
  private static final String UPDATED_LIEU_NAISSANCE = "BBBBBBBBBB";

  private static final SituationMatrimoniale DEFAULT_SITUATION_MATRIMONIALE = SituationMatrimoniale.MARIE;
  private static final SituationMatrimoniale UPDATED_SITUATION_MATRIMONIALE = SituationMatrimoniale.CELIBATAIRE;

  private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
  private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
  private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
  private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

  private static final String DEFAULT_PAYS_ORIGINE = "AAAAAAAAAA";
  private static final String UPDATED_PAYS_ORIGINE = "BBBBBBBBBB";

  private static final Boolean DEFAULT_VALIDITE = false;
  private static final Boolean UPDATED_VALIDITE = true;

  private static final String ENTITY_API_URL = "/api/personnel";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_SEARCH_API_URL = "/api/_search/personnel";

  @Autowired
  private PersonnelRepository personnelRepository;

  @Autowired
  private PersonnelMapper personnelMapper;

  @Autowired
  private PersonnelSearchRepository personnelSearchRepository;

  @Autowired
  private MockMvc restPersonnelMockMvc;

  private Personnel personnel;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Personnel createEntity() {
    Personnel personnel = new Personnel()
      .matricule(DEFAULT_MATRICULE)
      .sexe(DEFAULT_SEXE)
      .nomActeur(DEFAULT_NOM_ACTEUR)
      .prenomsActeur(DEFAULT_PRENOMS_ACTEUR)
      .dateNaissance(DEFAULT_DATE_NAISSANCE)
      .lieuNaissance(DEFAULT_LIEU_NAISSANCE)
      .situationMatrimoniale(DEFAULT_SITUATION_MATRIMONIALE)
      .photo(DEFAULT_PHOTO)
      .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
      .paysOrigine(DEFAULT_PAYS_ORIGINE)
      .validite(DEFAULT_VALIDITE);
    return personnel;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Personnel createUpdatedEntity() {
    Personnel personnel = new Personnel()
      .matricule(UPDATED_MATRICULE)
      .sexe(UPDATED_SEXE)
      .nomActeur(UPDATED_NOM_ACTEUR)
      .prenomsActeur(UPDATED_PRENOMS_ACTEUR)
      .dateNaissance(UPDATED_DATE_NAISSANCE)
      .lieuNaissance(UPDATED_LIEU_NAISSANCE)
      .situationMatrimoniale(UPDATED_SITUATION_MATRIMONIALE)
      .photo(UPDATED_PHOTO)
      .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
      .paysOrigine(UPDATED_PAYS_ORIGINE)
      .validite(UPDATED_VALIDITE);
    return personnel;
  }

  @AfterEach
  public void cleanupElasticSearchRepository() {
    personnelSearchRepository.deleteAll();
    assertThat(personnelSearchRepository.count()).isEqualTo(0);
  }

  @BeforeEach
  public void initTest() {
    personnelRepository.deleteAll();
    personnel = createEntity();
  }

  @Test
  void createPersonnel() throws Exception {
    int databaseSizeBeforeCreate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);
    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isCreated());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeCreate + 1);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
      });
    Personnel testPersonnel = personnelList.get(personnelList.size() - 1);
    assertThat(testPersonnel.getMatricule()).isEqualTo(DEFAULT_MATRICULE);
    assertThat(testPersonnel.getSexe()).isEqualTo(DEFAULT_SEXE);
    assertThat(testPersonnel.getNomActeur()).isEqualTo(DEFAULT_NOM_ACTEUR);
    assertThat(testPersonnel.getPrenomsActeur()).isEqualTo(DEFAULT_PRENOMS_ACTEUR);
    assertThat(testPersonnel.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
    assertThat(testPersonnel.getLieuNaissance()).isEqualTo(DEFAULT_LIEU_NAISSANCE);
    assertThat(testPersonnel.getSituationMatrimoniale()).isEqualTo(DEFAULT_SITUATION_MATRIMONIALE);
    assertThat(testPersonnel.getPhoto()).isEqualTo(DEFAULT_PHOTO);
    assertThat(testPersonnel.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    assertThat(testPersonnel.getPaysOrigine()).isEqualTo(DEFAULT_PAYS_ORIGINE);
    assertThat(testPersonnel.getValidite()).isEqualTo(DEFAULT_VALIDITE);
  }

  @Test
  void createPersonnelWithExistingId() throws Exception {
    // Create the Personnel with an existing ID
    personnel.setId("existing_id");
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    int databaseSizeBeforeCreate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());

    // An entity with an existing ID cannot be created, so this API call must fail
    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isBadRequest());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeCreate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkSexeIsRequired() throws Exception {
    int databaseSizeBeforeTest = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    // set the field null
    personnel.setSexe(null);

    // Create the Personnel, which fails.
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isBadRequest());

    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkNomActeurIsRequired() throws Exception {
    int databaseSizeBeforeTest = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    // set the field null
    personnel.setNomActeur(null);

    // Create the Personnel, which fails.
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isBadRequest());

    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkPrenomsActeurIsRequired() throws Exception {
    int databaseSizeBeforeTest = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    // set the field null
    personnel.setPrenomsActeur(null);

    // Create the Personnel, which fails.
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isBadRequest());

    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void checkLieuNaissanceIsRequired() throws Exception {
    int databaseSizeBeforeTest = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    // set the field null
    personnel.setLieuNaissance(null);

    // Create the Personnel, which fails.
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    restPersonnelMockMvc
      .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isBadRequest());

    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeTest);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void getAllPersonnel() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);

    // Get all the personnelList
    restPersonnelMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(personnel.getId())))
      .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
      .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
      .andExpect(jsonPath("$.[*].nomActeur").value(hasItem(DEFAULT_NOM_ACTEUR)))
      .andExpect(jsonPath("$.[*].prenomsActeur").value(hasItem(DEFAULT_PRENOMS_ACTEUR)))
      .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
      .andExpect(jsonPath("$.[*].lieuNaissance").value(hasItem(DEFAULT_LIEU_NAISSANCE)))
      .andExpect(jsonPath("$.[*].situationMatrimoniale").value(hasItem(DEFAULT_SITUATION_MATRIMONIALE.toString())))
      .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
      .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
      .andExpect(jsonPath("$.[*].paysOrigine").value(hasItem(DEFAULT_PAYS_ORIGINE)))
      .andExpect(jsonPath("$.[*].validite").value(hasItem(DEFAULT_VALIDITE.booleanValue())));
  }

  @Test
  void getPersonnel() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);

    // Get the personnel
    restPersonnelMockMvc
      .perform(get(ENTITY_API_URL_ID, personnel.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(personnel.getId()))
      .andExpect(jsonPath("$.matricule").value(DEFAULT_MATRICULE))
      .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE.toString()))
      .andExpect(jsonPath("$.nomActeur").value(DEFAULT_NOM_ACTEUR))
      .andExpect(jsonPath("$.prenomsActeur").value(DEFAULT_PRENOMS_ACTEUR))
      .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
      .andExpect(jsonPath("$.lieuNaissance").value(DEFAULT_LIEU_NAISSANCE))
      .andExpect(jsonPath("$.situationMatrimoniale").value(DEFAULT_SITUATION_MATRIMONIALE.toString()))
      .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
      .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
      .andExpect(jsonPath("$.paysOrigine").value(DEFAULT_PAYS_ORIGINE))
      .andExpect(jsonPath("$.validite").value(DEFAULT_VALIDITE.booleanValue()));
  }

  @Test
  void getNonExistingPersonnel() throws Exception {
    // Get the personnel
    restPersonnelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  void putExistingPersonnel() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);

    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    personnelSearchRepository.save(personnel);
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());

    // Update the personnel
    Personnel updatedPersonnel = personnelRepository.findById(personnel.getId()).get();
    updatedPersonnel
      .matricule(UPDATED_MATRICULE)
      .sexe(UPDATED_SEXE)
      .nomActeur(UPDATED_NOM_ACTEUR)
      .prenomsActeur(UPDATED_PRENOMS_ACTEUR)
      .dateNaissance(UPDATED_DATE_NAISSANCE)
      .lieuNaissance(UPDATED_LIEU_NAISSANCE)
      .situationMatrimoniale(UPDATED_SITUATION_MATRIMONIALE)
      .photo(UPDATED_PHOTO)
      .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
      .paysOrigine(UPDATED_PAYS_ORIGINE)
      .validite(UPDATED_VALIDITE);
    PersonnelDTO personnelDTO = personnelMapper.toDto(updatedPersonnel);

    restPersonnelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, personnelDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(personnelDTO))
      )
      .andExpect(status().isOk());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    Personnel testPersonnel = personnelList.get(personnelList.size() - 1);
    assertThat(testPersonnel.getMatricule()).isEqualTo(UPDATED_MATRICULE);
    assertThat(testPersonnel.getSexe()).isEqualTo(UPDATED_SEXE);
    assertThat(testPersonnel.getNomActeur()).isEqualTo(UPDATED_NOM_ACTEUR);
    assertThat(testPersonnel.getPrenomsActeur()).isEqualTo(UPDATED_PRENOMS_ACTEUR);
    assertThat(testPersonnel.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
    assertThat(testPersonnel.getLieuNaissance()).isEqualTo(UPDATED_LIEU_NAISSANCE);
    assertThat(testPersonnel.getSituationMatrimoniale()).isEqualTo(UPDATED_SITUATION_MATRIMONIALE);
    assertThat(testPersonnel.getPhoto()).isEqualTo(UPDATED_PHOTO);
    assertThat(testPersonnel.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    assertThat(testPersonnel.getPaysOrigine()).isEqualTo(UPDATED_PAYS_ORIGINE);
    assertThat(testPersonnel.getValidite()).isEqualTo(UPDATED_VALIDITE);
    await()
      .atMost(5, TimeUnit.SECONDS)
      .untilAsserted(() -> {
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
        List<Personnel> personnelSearchList = IterableUtils.toList(personnelSearchRepository.findAll());
        Personnel testPersonnelSearch = personnelSearchList.get(searchDatabaseSizeAfter - 1);
        assertThat(testPersonnelSearch.getMatricule()).isEqualTo(UPDATED_MATRICULE);
        assertThat(testPersonnelSearch.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testPersonnelSearch.getNomActeur()).isEqualTo(UPDATED_NOM_ACTEUR);
        assertThat(testPersonnelSearch.getPrenomsActeur()).isEqualTo(UPDATED_PRENOMS_ACTEUR);
        assertThat(testPersonnelSearch.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
        assertThat(testPersonnelSearch.getLieuNaissance()).isEqualTo(UPDATED_LIEU_NAISSANCE);
        assertThat(testPersonnelSearch.getSituationMatrimoniale()).isEqualTo(UPDATED_SITUATION_MATRIMONIALE);
        assertThat(testPersonnelSearch.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testPersonnelSearch.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testPersonnelSearch.getPaysOrigine()).isEqualTo(UPDATED_PAYS_ORIGINE);
        assertThat(testPersonnelSearch.getValidite()).isEqualTo(UPDATED_VALIDITE);
      });
  }

  @Test
  void putNonExistingPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, personnelDTO.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(personnelDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithIdMismatchPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(personnelDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void putWithMissingIdPathParamPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void partialUpdatePersonnelWithPatch() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);

    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();

    // Update the personnel using partial update
    Personnel partialUpdatedPersonnel = new Personnel();
    partialUpdatedPersonnel.setId(personnel.getId());

    partialUpdatedPersonnel
      .sexe(UPDATED_SEXE)
      .nomActeur(UPDATED_NOM_ACTEUR)
      .situationMatrimoniale(UPDATED_SITUATION_MATRIMONIALE)
      .photo(UPDATED_PHOTO)
      .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
      .paysOrigine(UPDATED_PAYS_ORIGINE);

    restPersonnelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPersonnel.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonnel))
      )
      .andExpect(status().isOk());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    Personnel testPersonnel = personnelList.get(personnelList.size() - 1);
    assertThat(testPersonnel.getMatricule()).isEqualTo(DEFAULT_MATRICULE);
    assertThat(testPersonnel.getSexe()).isEqualTo(UPDATED_SEXE);
    assertThat(testPersonnel.getNomActeur()).isEqualTo(UPDATED_NOM_ACTEUR);
    assertThat(testPersonnel.getPrenomsActeur()).isEqualTo(DEFAULT_PRENOMS_ACTEUR);
    assertThat(testPersonnel.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
    assertThat(testPersonnel.getLieuNaissance()).isEqualTo(DEFAULT_LIEU_NAISSANCE);
    assertThat(testPersonnel.getSituationMatrimoniale()).isEqualTo(UPDATED_SITUATION_MATRIMONIALE);
    assertThat(testPersonnel.getPhoto()).isEqualTo(UPDATED_PHOTO);
    assertThat(testPersonnel.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    assertThat(testPersonnel.getPaysOrigine()).isEqualTo(UPDATED_PAYS_ORIGINE);
    assertThat(testPersonnel.getValidite()).isEqualTo(DEFAULT_VALIDITE);
  }

  @Test
  void fullUpdatePersonnelWithPatch() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);

    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();

    // Update the personnel using partial update
    Personnel partialUpdatedPersonnel = new Personnel();
    partialUpdatedPersonnel.setId(personnel.getId());

    partialUpdatedPersonnel
      .matricule(UPDATED_MATRICULE)
      .sexe(UPDATED_SEXE)
      .nomActeur(UPDATED_NOM_ACTEUR)
      .prenomsActeur(UPDATED_PRENOMS_ACTEUR)
      .dateNaissance(UPDATED_DATE_NAISSANCE)
      .lieuNaissance(UPDATED_LIEU_NAISSANCE)
      .situationMatrimoniale(UPDATED_SITUATION_MATRIMONIALE)
      .photo(UPDATED_PHOTO)
      .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
      .paysOrigine(UPDATED_PAYS_ORIGINE)
      .validite(UPDATED_VALIDITE);

    restPersonnelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedPersonnel.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonnel))
      )
      .andExpect(status().isOk());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    Personnel testPersonnel = personnelList.get(personnelList.size() - 1);
    assertThat(testPersonnel.getMatricule()).isEqualTo(UPDATED_MATRICULE);
    assertThat(testPersonnel.getSexe()).isEqualTo(UPDATED_SEXE);
    assertThat(testPersonnel.getNomActeur()).isEqualTo(UPDATED_NOM_ACTEUR);
    assertThat(testPersonnel.getPrenomsActeur()).isEqualTo(UPDATED_PRENOMS_ACTEUR);
    assertThat(testPersonnel.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
    assertThat(testPersonnel.getLieuNaissance()).isEqualTo(UPDATED_LIEU_NAISSANCE);
    assertThat(testPersonnel.getSituationMatrimoniale()).isEqualTo(UPDATED_SITUATION_MATRIMONIALE);
    assertThat(testPersonnel.getPhoto()).isEqualTo(UPDATED_PHOTO);
    assertThat(testPersonnel.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    assertThat(testPersonnel.getPaysOrigine()).isEqualTo(UPDATED_PAYS_ORIGINE);
    assertThat(testPersonnel.getValidite()).isEqualTo(UPDATED_VALIDITE);
  }

  @Test
  void patchNonExistingPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, personnelDTO.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(personnelDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithIdMismatchPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(personnelDTO))
      )
      .andExpect(status().isBadRequest());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void patchWithMissingIdPathParamPersonnel() throws Exception {
    int databaseSizeBeforeUpdate = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    personnel.setId(UUID.randomUUID().toString());

    // Create the Personnel
    PersonnelDTO personnelDTO = personnelMapper.toDto(personnel);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restPersonnelMockMvc
      .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(personnelDTO)))
      .andExpect(status().isMethodNotAllowed());

    // Validate the Personnel in the database
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeUpdate);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
  }

  @Test
  void deletePersonnel() throws Exception {
    // Initialize the database
    personnelRepository.save(personnel);
    personnelRepository.save(personnel);
    personnelSearchRepository.save(personnel);

    int databaseSizeBeforeDelete = personnelRepository.findAll().size();
    int searchDatabaseSizeBefore = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

    // Delete the personnel
    restPersonnelMockMvc
      .perform(delete(ENTITY_API_URL_ID, personnel.getId()).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Personnel> personnelList = personnelRepository.findAll();
    assertThat(personnelList).hasSize(databaseSizeBeforeDelete - 1);
    int searchDatabaseSizeAfter = IterableUtil.sizeOf(personnelSearchRepository.findAll());
    assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
  }

  @Test
  void searchPersonnel() throws Exception {
    // Initialize the database
    personnel = personnelRepository.save(personnel);
    personnelSearchRepository.save(personnel);

    // Search the personnel
    restPersonnelMockMvc
      .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + personnel.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.[*].id").value(hasItem(personnel.getId())))
      .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
      .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
      .andExpect(jsonPath("$.[*].nomActeur").value(hasItem(DEFAULT_NOM_ACTEUR)))
      .andExpect(jsonPath("$.[*].prenomsActeur").value(hasItem(DEFAULT_PRENOMS_ACTEUR)))
      .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
      .andExpect(jsonPath("$.[*].lieuNaissance").value(hasItem(DEFAULT_LIEU_NAISSANCE)))
      .andExpect(jsonPath("$.[*].situationMatrimoniale").value(hasItem(DEFAULT_SITUATION_MATRIMONIALE.toString())))
      .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
      .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
      .andExpect(jsonPath("$.[*].paysOrigine").value(hasItem(DEFAULT_PAYS_ORIGINE)))
      .andExpect(jsonPath("$.[*].validite").value(hasItem(DEFAULT_VALIDITE.booleanValue())));
  }
}
