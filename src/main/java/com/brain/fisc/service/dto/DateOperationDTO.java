package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.brain.fisc.domain.DateOperation} entity.
 */
@Schema(description = "DateOperation entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DateOperationDTO implements Serializable {

  private String id;

  private LocalDate datoperation;

  private PeriodeDTO periode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getDatoperation() {
    return datoperation;
  }

  public void setDatoperation(LocalDate datoperation) {
    this.datoperation = datoperation;
  }

  public PeriodeDTO getPeriode() {
    return periode;
  }

  public void setPeriode(PeriodeDTO periode) {
    this.periode = periode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DateOperationDTO)) {
      return false;
    }

    DateOperationDTO dateOperationDTO = (DateOperationDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, dateOperationDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "DateOperationDTO{" +
            "id='" + getId() + "'" +
            ", datoperation='" + getDatoperation() + "'" +
            ", periode=" + getPeriode() +
            "}";
    }
}
