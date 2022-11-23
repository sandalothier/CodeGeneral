package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Poste} entity.
 */
@Schema(description = "Poste entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PosteDTO implements Serializable {

  private String id;

  @Size(max = 25)
  private String intPoste;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntPoste() {
    return intPoste;
  }

  public void setIntPoste(String intPoste) {
    this.intPoste = intPoste;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PosteDTO)) {
      return false;
    }

    PosteDTO posteDTO = (PosteDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, posteDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PosteDTO{" +
            "id='" + getId() + "'" +
            ", intPoste='" + getIntPoste() + "'" +
            "}";
    }
}
