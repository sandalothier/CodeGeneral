package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Pointage} entity.
 */
@Schema(description = "Pointage entity.\n@author sanda\nCette table est en lien avec\nla table emploi du temps et le calendrier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PointageDTO implements Serializable {

  private String id;

  @NotNull
  private LocalDate heurArrivee;

  @NotNull
  private LocalDate heurDepart;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getHeurArrivee() {
    return heurArrivee;
  }

  public void setHeurArrivee(LocalDate heurArrivee) {
    this.heurArrivee = heurArrivee;
  }

  public LocalDate getHeurDepart() {
    return heurDepart;
  }

  public void setHeurDepart(LocalDate heurDepart) {
    this.heurDepart = heurDepart;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PointageDTO)) {
      return false;
    }

    PointageDTO pointageDTO = (PointageDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, pointageDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PointageDTO{" +
            "id='" + getId() + "'" +
            ", heurArrivee='" + getHeurArrivee() + "'" +
            ", heurDepart='" + getHeurDepart() + "'" +
            "}";
    }
}
