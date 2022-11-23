package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Periode} entity.
 */
@Schema(description = "Periode entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PeriodeDTO implements Serializable {

  private String id;

  @Size(max = 25)
  private String intPeriode;

  private LocalDate debut;

  private LocalDate fin;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntPeriode() {
    return intPeriode;
  }

  public void setIntPeriode(String intPeriode) {
    this.intPeriode = intPeriode;
  }

  public LocalDate getDebut() {
    return debut;
  }

  public void setDebut(LocalDate debut) {
    this.debut = debut;
  }

  public LocalDate getFin() {
    return fin;
  }

  public void setFin(LocalDate fin) {
    this.fin = fin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PeriodeDTO)) {
      return false;
    }

    PeriodeDTO periodeDTO = (PeriodeDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, periodeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PeriodeDTO{" +
            "id='" + getId() + "'" +
            ", intPeriode='" + getIntPeriode() + "'" +
            ", debut='" + getDebut() + "'" +
            ", fin='" + getFin() + "'" +
            "}";
    }
}
