package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.ContratEtablis} entity.
 */
@Schema(description = "ContratEtablis entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContratEtablisDTO implements Serializable {

  private String id;

  @NotNull
  @Size(max = 10)
  private String refContrat;

  private LocalDate dateEtablissement;

  private TypeContratDeTravailDTO intTypeContrat;

  private PeriodeDTO intPeriode;

  private PersonnelDTO personnel;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefContrat() {
    return refContrat;
  }

  public void setRefContrat(String refContrat) {
    this.refContrat = refContrat;
  }

  public LocalDate getDateEtablissement() {
    return dateEtablissement;
  }

  public void setDateEtablissement(LocalDate dateEtablissement) {
    this.dateEtablissement = dateEtablissement;
  }

  public TypeContratDeTravailDTO getIntTypeContrat() {
    return intTypeContrat;
  }

  public void setIntTypeContrat(TypeContratDeTravailDTO intTypeContrat) {
    this.intTypeContrat = intTypeContrat;
  }

  public PeriodeDTO getIntPeriode() {
    return intPeriode;
  }

  public void setIntPeriode(PeriodeDTO intPeriode) {
    this.intPeriode = intPeriode;
  }

  public PersonnelDTO getPersonnel() {
    return personnel;
  }

  public void setPersonnel(PersonnelDTO personnel) {
    this.personnel = personnel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ContratEtablisDTO)) {
      return false;
    }

    ContratEtablisDTO contratEtablisDTO = (ContratEtablisDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, contratEtablisDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ContratEtablisDTO{" +
            "id='" + getId() + "'" +
            ", refContrat='" + getRefContrat() + "'" +
            ", dateEtablissement='" + getDateEtablissement() + "'" +
            ", intTypeContrat=" + getIntTypeContrat() +
            ", intPeriode=" + getIntPeriode() +
            ", personnel=" + getPersonnel() +
            "}";
    }
}
