package com.brain.fisc.domain;

import com.brain.fisc.domain.enumeration.Sexe;
import com.brain.fisc.domain.enumeration.SituationMatrimoniale;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Personnel entity.\n@author sanda\nCe sont les individus\nCette table extends la table Acteur
 */
@Document(collection = "personnel")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "personnel")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Personnel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 10)
  @Field("matricule")
  private String matricule;

  @NotNull
  @Field("sexe")
  private Sexe sexe;

  @NotNull
  @Size(max = 20)
  @Field("nom_acteur")
  private String nomActeur;

  @NotNull
  @Size(max = 25)
  @Field("prenoms_acteur")
  private String prenomsActeur;

  @Field("date_naissance")
  private LocalDate dateNaissance;

  @NotNull
  @Size(max = 25)
  @Field("lieu_naissance")
  private String lieuNaissance;

  @Field("situation_matrimoniale")
  private SituationMatrimoniale situationMatrimoniale;

  @Field("photo")
  private byte[] photo;

  @Field("photo_content_type")
  private String photoContentType;

  @Size(max = 25)
  @Field("pays_origine")
  private String paysOrigine;

  @Field("validite")
  private Boolean validite;

  @DBRef
  @Field("codeDiplome")
  private Diplome codeDiplome;

  @DBRef
  @Field("cel")
  private Adresse cel;

  @DBRef
  @Field("intPoste")
  private Poste intPoste;

  @DBRef
  @Field("refContrat")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "intTypeContrat", "intPeriode", "personnel" }, allowSetters = true)
  private Set<ContratEtablis> refContrats = new HashSet<>();

  @DBRef
  @Field("societe")
  @JsonIgnoreProperties(value = { "matricules", "intTypeDocs", "intSuccursales" }, allowSetters = true)
  private Societe societe;

  @DBRef
  @Field("pointage")
  @JsonIgnoreProperties(value = { "matricules" }, allowSetters = true)
  private Pointage pointage;

  @DBRef
  @Field("equipe")
  @JsonIgnoreProperties(value = { "matricules" }, allowSetters = true)
  private Equipe equipe;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Personnel id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMatricule() {
    return this.matricule;
  }

  public Personnel matricule(String matricule) {
    this.setMatricule(matricule);
    return this;
  }

  public void setMatricule(String matricule) {
    this.matricule = matricule;
  }

  public Sexe getSexe() {
    return this.sexe;
  }

  public Personnel sexe(Sexe sexe) {
    this.setSexe(sexe);
    return this;
  }

  public void setSexe(Sexe sexe) {
    this.sexe = sexe;
  }

  public String getNomActeur() {
    return this.nomActeur;
  }

  public Personnel nomActeur(String nomActeur) {
    this.setNomActeur(nomActeur);
    return this;
  }

  public void setNomActeur(String nomActeur) {
    this.nomActeur = nomActeur;
  }

  public String getPrenomsActeur() {
    return this.prenomsActeur;
  }

  public Personnel prenomsActeur(String prenomsActeur) {
    this.setPrenomsActeur(prenomsActeur);
    return this;
  }

  public void setPrenomsActeur(String prenomsActeur) {
    this.prenomsActeur = prenomsActeur;
  }

  public LocalDate getDateNaissance() {
    return this.dateNaissance;
  }

  public Personnel dateNaissance(LocalDate dateNaissance) {
    this.setDateNaissance(dateNaissance);
    return this;
  }

  public void setDateNaissance(LocalDate dateNaissance) {
    this.dateNaissance = dateNaissance;
  }

  public String getLieuNaissance() {
    return this.lieuNaissance;
  }

  public Personnel lieuNaissance(String lieuNaissance) {
    this.setLieuNaissance(lieuNaissance);
    return this;
  }

  public void setLieuNaissance(String lieuNaissance) {
    this.lieuNaissance = lieuNaissance;
  }

  public SituationMatrimoniale getSituationMatrimoniale() {
    return this.situationMatrimoniale;
  }

  public Personnel situationMatrimoniale(SituationMatrimoniale situationMatrimoniale) {
    this.setSituationMatrimoniale(situationMatrimoniale);
    return this;
  }

  public void setSituationMatrimoniale(SituationMatrimoniale situationMatrimoniale) {
    this.situationMatrimoniale = situationMatrimoniale;
  }

  public byte[] getPhoto() {
    return this.photo;
  }

  public Personnel photo(byte[] photo) {
    this.setPhoto(photo);
    return this;
  }

  public void setPhoto(byte[] photo) {
    this.photo = photo;
  }

  public String getPhotoContentType() {
    return this.photoContentType;
  }

  public Personnel photoContentType(String photoContentType) {
    this.photoContentType = photoContentType;
    return this;
  }

  public void setPhotoContentType(String photoContentType) {
    this.photoContentType = photoContentType;
  }

  public String getPaysOrigine() {
    return this.paysOrigine;
  }

  public Personnel paysOrigine(String paysOrigine) {
    this.setPaysOrigine(paysOrigine);
    return this;
  }

  public void setPaysOrigine(String paysOrigine) {
    this.paysOrigine = paysOrigine;
  }

  public Boolean getValidite() {
    return this.validite;
  }

  public Personnel validite(Boolean validite) {
    this.setValidite(validite);
    return this;
  }

  public void setValidite(Boolean validite) {
    this.validite = validite;
  }

  public Diplome getCodeDiplome() {
    return this.codeDiplome;
  }

  public void setCodeDiplome(Diplome diplome) {
    this.codeDiplome = diplome;
  }

  public Personnel codeDiplome(Diplome diplome) {
    this.setCodeDiplome(diplome);
    return this;
  }

  public Adresse getCel() {
    return this.cel;
  }

  public void setCel(Adresse adresse) {
    this.cel = adresse;
  }

  public Personnel cel(Adresse adresse) {
    this.setCel(adresse);
    return this;
  }

  public Poste getIntPoste() {
    return this.intPoste;
  }

  public void setIntPoste(Poste poste) {
    this.intPoste = poste;
  }

  public Personnel intPoste(Poste poste) {
    this.setIntPoste(poste);
    return this;
  }

  public Set<ContratEtablis> getRefContrats() {
    return this.refContrats;
  }

  public void setRefContrats(Set<ContratEtablis> contratEtablis) {
    if (this.refContrats != null) {
      this.refContrats.forEach(i -> i.setPersonnel(null));
    }
    if (contratEtablis != null) {
      contratEtablis.forEach(i -> i.setPersonnel(this));
    }
    this.refContrats = contratEtablis;
  }

  public Personnel refContrats(Set<ContratEtablis> contratEtablis) {
    this.setRefContrats(contratEtablis);
    return this;
  }

  public Personnel addRefContrat(ContratEtablis contratEtablis) {
    this.refContrats.add(contratEtablis);
    contratEtablis.setPersonnel(this);
    return this;
  }

  public Personnel removeRefContrat(ContratEtablis contratEtablis) {
    this.refContrats.remove(contratEtablis);
    contratEtablis.setPersonnel(null);
    return this;
  }

  public Societe getSociete() {
    return this.societe;
  }

  public void setSociete(Societe societe) {
    this.societe = societe;
  }

  public Personnel societe(Societe societe) {
    this.setSociete(societe);
    return this;
  }

  public Pointage getPointage() {
    return this.pointage;
  }

  public void setPointage(Pointage pointage) {
    this.pointage = pointage;
  }

  public Personnel pointage(Pointage pointage) {
    this.setPointage(pointage);
    return this;
  }

  public Equipe getEquipe() {
    return this.equipe;
  }

  public void setEquipe(Equipe equipe) {
    this.equipe = equipe;
  }

  public Personnel equipe(Equipe equipe) {
    this.setEquipe(equipe);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Personnel)) {
      return false;
    }
    return id != null && id.equals(((Personnel) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Personnel{" +
            "id=" + getId() +
            ", matricule='" + getMatricule() + "'" +
            ", sexe='" + getSexe() + "'" +
            ", nomActeur='" + getNomActeur() + "'" +
            ", prenomsActeur='" + getPrenomsActeur() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", lieuNaissance='" + getLieuNaissance() + "'" +
            ", situationMatrimoniale='" + getSituationMatrimoniale() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", paysOrigine='" + getPaysOrigine() + "'" +
            ", validite='" + getValidite() + "'" +
            "}";
    }
}
