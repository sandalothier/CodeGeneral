package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.TypeDocument} entity.
 */
@Schema(description = "TypeDocument entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeDocumentDTO implements Serializable {

  private String id;

  @Size(max = 50)
  private String intTypeDoc;

  private SocieteDTO societe;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntTypeDoc() {
    return intTypeDoc;
  }

  public void setIntTypeDoc(String intTypeDoc) {
    this.intTypeDoc = intTypeDoc;
  }

  public SocieteDTO getSociete() {
    return societe;
  }

  public void setSociete(SocieteDTO societe) {
    this.societe = societe;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypeDocumentDTO)) {
      return false;
    }

    TypeDocumentDTO typeDocumentDTO = (TypeDocumentDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, typeDocumentDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TypeDocumentDTO{" +
            "id='" + getId() + "'" +
            ", intTypeDoc='" + getIntTypeDoc() + "'" +
            ", societe=" + getSociete() +
            "}";
    }
}
