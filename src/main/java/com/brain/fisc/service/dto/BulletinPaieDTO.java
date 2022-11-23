package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.BulletinPaie} entity.
 */
@Schema(
  description = "BulletinPaie entity.\n@author sanda\nC'est le document qui est délivré au Agent justifiant leur dette vis à vis\nde l'administration"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BulletinPaieDTO implements Serializable {

  private String id;

  @Size(max = 10)
  private String refBulletin;

  private PeriodeDTO periode;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefBulletin() {
    return refBulletin;
  }

  public void setRefBulletin(String refBulletin) {
    this.refBulletin = refBulletin;
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
    if (!(o instanceof BulletinPaieDTO)) {
      return false;
    }

    BulletinPaieDTO bulletinPaieDTO = (BulletinPaieDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, bulletinPaieDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BulletinPaieDTO{" +
            "id='" + getId() + "'" +
            ", refBulletin='" + getRefBulletin() + "'" +
            ", periode=" + getPeriode() +
            "}";
    }
}
