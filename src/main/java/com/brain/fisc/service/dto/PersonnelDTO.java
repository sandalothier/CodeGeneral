package com.brain.fisc.service.dto;

import com.brain.fisc.domain.enumeration.Sexe;
import com.brain.fisc.domain.enumeration.SituationMatrimoniale;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Personnel} entity.
 */
@Schema(description = "Personnel entity.\n@author sanda\nCe sont les individus\nCette table extends la table Acteur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonnelDTO implements Serializable {

  private String id;

  @Size(max = 10)
  private String matricule;

  @NotNull
  private Sexe sexe;

  @NotNull
  @Size(max = 20)
  private String nomActeur;

  @NotNull
  @Size(max = 25)
  private String prenomsActeur;

  private LocalDate dateNaissance;

  @NotNull
  @Size(max = 25)
  private String lieuNaissance;

  private SituationMatrimoniale situationMatrimoniale;

  private byte[] photo;

  private String photoContentType;

  @Size(max = 25)
  private String paysOrigine;

  private Boolean validite;

  private DiplomeDTO codeDiplome;

  private AdresseDTO cel;

  private PosteDTO intPoste;

  private SocieteDTO societe;

  private PointageDTO pointage;

  private EquipeDTO equipe;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMatricule() {
    return matricule;
  }

  public void setMatricule(String matricule) {
    this.matricule = matricule;
  }

  public Sexe getSexe() {
    return sexe;
  }

  public void setSexe(Sexe sexe) {
    this.sexe = sexe;
  }

  public String getNomActeur() {
    return nomActeur;
  }

  public void setNomActeur(String nomActeur) {
    this.nomActeur = nomActeur;
  }

  public String getPrenomsActeur() {
    return prenomsActeur;
  }

  public void setPrenomsActeur(String prenomsActeur) {
    this.prenomsActeur = prenomsActeur;
  }

  public LocalDate getDateNaissance() {
    return dateNaissance;
  }

  public void setDateNaissance(LocalDate dateNaissance) {
    this.dateNaissance = dateNaissance;
  }

  public String getLieuNaissance() {
    return lieuNaissance;
  }

  public void setLieuNaissance(String lieuNaissance) {
    this.lieuNaissance = lieuNaissance;
  }

  public SituationMatrimoniale getSituationMatrimoniale() {
    return situationMatrimoniale;
  }

  public void setSituationMatrimoniale(SituationMatrimoniale situationMatrimoniale) {
    this.situationMatrimoniale = situationMatrimoniale;
  }

  public byte[] getPhoto() {
    return photo;
  }

  public void setPhoto(byte[] photo) {
    this.photo = photo;
  }

  public String getPhotoContentType() {
    return photoContentType;
  }

  public void setPhotoContentType(String photoContentType) {
    this.photoContentType = photoContentType;
  }

  public String getPaysOrigine() {
    return paysOrigine;
  }

  public void setPaysOrigine(String paysOrigine) {
    this.paysOrigine = paysOrigine;
  }

  public Boolean getValidite() {
    return validite;
  }

  public void setValidite(Boolean validite) {
    this.validite = validite;
  }

  public DiplomeDTO getCodeDiplome() {
    return codeDiplome;
  }

  public void setCodeDiplome(DiplomeDTO codeDiplome) {
    this.codeDiplome = codeDiplome;
  }

  public AdresseDTO getCel() {
    return cel;
  }

  public void setCel(AdresseDTO cel) {
    this.cel = cel;
  }

  public PosteDTO getIntPoste() {
    return intPoste;
  }

  public void setIntPoste(PosteDTO intPoste) {
    this.intPoste = intPoste;
  }

  public SocieteDTO getSociete() {
    return societe;
  }

  public void setSociete(SocieteDTO societe) {
    this.societe = societe;
  }

  public PointageDTO getPointage() {
    return pointage;
  }

  public void setPointage(PointageDTO pointage) {
    this.pointage = pointage;
  }

  public EquipeDTO getEquipe() {
    return equipe;
  }

  public void setEquipe(EquipeDTO equipe) {
    this.equipe = equipe;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PersonnelDTO)) {
      return false;
    }

    PersonnelDTO personnelDTO = (PersonnelDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, personnelDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PersonnelDTO{" +
            "id='" + getId() + "'" +
            ", matricule='" + getMatricule() + "'" +
            ", sexe='" + getSexe() + "'" +
            ", nomActeur='" + getNomActeur() + "'" +
            ", prenomsActeur='" + getPrenomsActeur() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", lieuNaissance='" + getLieuNaissance() + "'" +
            ", situationMatrimoniale='" + getSituationMatrimoniale() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", paysOrigine='" + getPaysOrigine() + "'" +
            ", validite='" + getValidite() + "'" +
            ", codeDiplome=" + getCodeDiplome() +
            ", cel=" + getCel() +
            ", intPoste=" + getIntPoste() +
            ", societe=" + getSociete() +
            ", pointage=" + getPointage() +
            ", equipe=" + getEquipe() +
            "}";
    }
}
