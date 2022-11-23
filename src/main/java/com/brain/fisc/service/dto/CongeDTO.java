package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Conge} entity.
 */
@Schema(
  description = "Conge entity.\n@author sanda\nCette table est liée aux permissions où ils déduisent ceux qui le sont.\nElle se constitue suivant des critères bien précis:\n- Type de contrat\n- Durée dans la fonction\n- Déduction\n- Augmentation suivantun cycle"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CongeDTO implements Serializable {

  private String id;

  @Size(max = 10)
  private String refConge;

  private PeriodeDTO periode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefConge() {
    return refConge;
  }

  public void setRefConge(String refConge) {
    this.refConge = refConge;
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
    if (!(o instanceof CongeDTO)) {
      return false;
    }

    CongeDTO congeDTO = (CongeDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, congeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "CongeDTO{" +
            "id='" + getId() + "'" +
            ", refConge='" + getRefConge() + "'" +
            ", periode=" + getPeriode() +
            "}";
    }
}
