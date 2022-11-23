package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.TypeContratDeTravail} entity.
 */
@Schema(description = "TypeContratDeTravail entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeContratDeTravailDTO implements Serializable {

  private String id;

  @Size(max = 25)
  private String intTypeContrat;

  @NotNull
  @Size(max = 50)
  private String description;

  @NotNull
  private Integer dureeMax;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntTypeContrat() {
    return intTypeContrat;
  }

  public void setIntTypeContrat(String intTypeContrat) {
    this.intTypeContrat = intTypeContrat;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDureeMax() {
    return dureeMax;
  }

  public void setDureeMax(Integer dureeMax) {
    this.dureeMax = dureeMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypeContratDeTravailDTO)) {
      return false;
    }

    TypeContratDeTravailDTO typeContratDeTravailDTO = (TypeContratDeTravailDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, typeContratDeTravailDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TypeContratDeTravailDTO{" +
            "id='" + getId() + "'" +
            ", intTypeContrat='" + getIntTypeContrat() + "'" +
            ", description='" + getDescription() + "'" +
            ", dureeMax=" + getDureeMax() +
            "}";
    }
}
