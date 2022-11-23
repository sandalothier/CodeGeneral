package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Societe} entity.
 */
@Schema(description = "Societe entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SocieteDTO implements Serializable {

  private String id;

  @NotNull
  @Size(max = 25)
  private String intSociete;

  @Size(max = 25)
  private String sigle;

  @NotNull
  @Size(max = 25)
  private String logo;

  @Size(max = 25)
  private String siege;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntSociete() {
    return intSociete;
  }

  public void setIntSociete(String intSociete) {
    this.intSociete = intSociete;
  }

  public String getSigle() {
    return sigle;
  }

  public void setSigle(String sigle) {
    this.sigle = sigle;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getSiege() {
    return siege;
  }

  public void setSiege(String siege) {
    this.siege = siege;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SocieteDTO)) {
      return false;
    }

    SocieteDTO societeDTO = (SocieteDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, societeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "SocieteDTO{" +
            "id='" + getId() + "'" +
            ", intSociete='" + getIntSociete() + "'" +
            ", sigle='" + getSigle() + "'" +
            ", logo='" + getLogo() + "'" +
            ", siege='" + getSiege() + "'" +
            "}";
    }
}
