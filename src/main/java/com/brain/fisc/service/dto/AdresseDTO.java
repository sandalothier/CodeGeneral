package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Adresse} entity.
 */
@Schema(description = "Adresse entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AdresseDTO implements Serializable {

  private String id;

  @Size(max = 8)
  private String cel;

  @Size(max = 8)
  private String tel;

  @Size(max = 25)
  private String region;

  @Size(max = 25)
  private String nomRue;

  @Size(max = 25)
  private String numRue;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCel() {
    return cel;
  }

  public void setCel(String cel) {
    this.cel = cel;
  }

  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getNomRue() {
    return nomRue;
  }

  public void setNomRue(String nomRue) {
    this.nomRue = nomRue;
  }

  public String getNumRue() {
    return numRue;
  }

  public void setNumRue(String numRue) {
    this.numRue = numRue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AdresseDTO)) {
      return false;
    }

    AdresseDTO adresseDTO = (AdresseDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, adresseDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AdresseDTO{" +
            "id='" + getId() + "'" +
            ", cel='" + getCel() + "'" +
            ", tel='" + getTel() + "'" +
            ", region='" + getRegion() + "'" +
            ", nomRue='" + getNomRue() + "'" +
            ", numRue='" + getNumRue() + "'" +
            "}";
    }
}
