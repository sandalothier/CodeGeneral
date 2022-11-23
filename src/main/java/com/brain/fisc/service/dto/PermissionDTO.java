package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Permission} entity.
 */
@Schema(
  description = "Permission entity.\n@author sanda\nLes motifs pour lesquels l'on prend légalement une permission\n- Décès\n- Natalité\n- Déménagement\n- Personnel\nCette table deduit le nombre de jour pris en congé si ceux-ci ne sont pas déductibles\nDéfinir la condition de déductibilité"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PermissionDTO implements Serializable {

  private String id;

  @NotNull
  @Size(max = 10)
  private String refpermission;

  @Size(max = 25)
  private String intPermission;

  @NotNull
  @Size(max = 25)
  private String motif;

  private Integer deductPerm;

  private DateOperationDTO dateOperation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefpermission() {
    return refpermission;
  }

  public void setRefpermission(String refpermission) {
    this.refpermission = refpermission;
  }

  public String getIntPermission() {
    return intPermission;
  }

  public void setIntPermission(String intPermission) {
    this.intPermission = intPermission;
  }

  public String getMotif() {
    return motif;
  }

  public void setMotif(String motif) {
    this.motif = motif;
  }

  public Integer getDeductPerm() {
    return deductPerm;
  }

  public void setDeductPerm(Integer deductPerm) {
    this.deductPerm = deductPerm;
  }

  public DateOperationDTO getDateOperation() {
    return dateOperation;
  }

  public void setDateOperation(DateOperationDTO dateOperation) {
    this.dateOperation = dateOperation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PermissionDTO)) {
      return false;
    }

    PermissionDTO permissionDTO = (PermissionDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, permissionDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "PermissionDTO{" +
            "id='" + getId() + "'" +
            ", refpermission='" + getRefpermission() + "'" +
            ", intPermission='" + getIntPermission() + "'" +
            ", motif='" + getMotif() + "'" +
            ", deductPerm=" + getDeductPerm() +
            ", dateOperation=" + getDateOperation() +
            "}";
    }
}
