package com.brain.fisc.service.dto;

import com.brain.fisc.domain.enumeration.TypeTraitement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.Traitement} entity.
 */
@Schema(description = "Traitement entity.\n@author sanda\nCe sont les sommmes que le Agent prend auprès de l'administration\n")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TraitementDTO implements Serializable {

  private String id;

  @Size(max = 10)
  private String refTraitement;

  private TypeTraitement typeTraitement;

  private Double montant;

  private DateOperationDTO dateOperation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefTraitement() {
    return refTraitement;
  }

  public void setRefTraitement(String refTraitement) {
    this.refTraitement = refTraitement;
  }

  public TypeTraitement getTypeTraitement() {
    return typeTraitement;
  }

  public void setTypeTraitement(TypeTraitement typeTraitement) {
    this.typeTraitement = typeTraitement;
  }

  public Double getMontant() {
    return montant;
  }

  public void setMontant(Double montant) {
    this.montant = montant;
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
    if (!(o instanceof TraitementDTO)) {
      return false;
    }

    TraitementDTO traitementDTO = (TraitementDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, traitementDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TraitementDTO{" +
            "id='" + getId() + "'" +
            ", refTraitement='" + getRefTraitement() + "'" +
            ", typeTraitement='" + getTypeTraitement() + "'" +
            ", montant=" + getMontant() +
            ", dateOperation=" + getDateOperation() +
            "}";
    }
}
