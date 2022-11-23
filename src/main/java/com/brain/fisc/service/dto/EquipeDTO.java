package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Equipe} entity.
 */
@Schema(description = "Equipe entity.\n@author sanda\nCette table est en relation avec celle acteur pour la constitution\ndes équipes.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipeDTO implements Serializable {

  private String id;

  @Size(max = 10)
  private String refEquipe;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefEquipe() {
    return refEquipe;
  }

  public void setRefEquipe(String refEquipe) {
    this.refEquipe = refEquipe;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EquipeDTO)) {
      return false;
    }

    EquipeDTO equipeDTO = (EquipeDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, equipeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EquipeDTO{" +
            "id='" + getId() + "'" +
            ", refEquipe='" + getRefEquipe() + "'" +
            "}";
    }
}
