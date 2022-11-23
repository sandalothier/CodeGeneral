package com.brain.fisc.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.brain.fisc.domain.ComposantDocument} entity.
 */
@Schema(description = "ComposantDocument entity.\n@author sanda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComposantDocumentDTO implements Serializable {

  private String id;

  @NotNull
  @Size(max = 25)
  private String intComposant;

  @Size(max = 50)
  private String titreComposant;

  private String contenu;

  private TypeDocumentDTO typeDocument;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntComposant() {
    return intComposant;
  }

  public void setIntComposant(String intComposant) {
    this.intComposant = intComposant;
  }

  public String getTitreComposant() {
    return titreComposant;
  }

  public void setTitreComposant(String titreComposant) {
    this.titreComposant = titreComposant;
  }

  public String getContenu() {
    return contenu;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  public TypeDocumentDTO getTypeDocument() {
    return typeDocument;
  }

  public void setTypeDocument(TypeDocumentDTO typeDocument) {
    this.typeDocument = typeDocument;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ComposantDocumentDTO)) {
      return false;
    }

    ComposantDocumentDTO composantDocumentDTO = (ComposantDocumentDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, composantDocumentDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ComposantDocumentDTO{" +
            "id='" + getId() + "'" +
            ", intComposant='" + getIntComposant() + "'" +
            ", titreComposant='" + getTitreComposant() + "'" +
            ", contenu='" + getContenu() + "'" +
            ", typeDocument=" + getTypeDocument() +
            "}";
    }
}
