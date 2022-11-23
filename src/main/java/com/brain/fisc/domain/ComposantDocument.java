package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * ComposantDocument entity.\n@author sanda
 */
@Document(collection = "composant_document")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "composantdocument")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComposantDocument implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 25)
  @Field("int_composant")
  private String intComposant;

  @Size(max = 50)
  @Field("titre_composant")
  private String titreComposant;

  @Field("contenu")
  private String contenu;

  @DBRef
  @Field("typeDocument")
  @JsonIgnoreProperties(value = { "intComposants", "societe" }, allowSetters = true)
  private TypeDocument typeDocument;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public ComposantDocument id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntComposant() {
    return this.intComposant;
  }

  public ComposantDocument intComposant(String intComposant) {
    this.setIntComposant(intComposant);
    return this;
  }

  public void setIntComposant(String intComposant) {
    this.intComposant = intComposant;
  }

  public String getTitreComposant() {
    return this.titreComposant;
  }

  public ComposantDocument titreComposant(String titreComposant) {
    this.setTitreComposant(titreComposant);
    return this;
  }

  public void setTitreComposant(String titreComposant) {
    this.titreComposant = titreComposant;
  }

  public String getContenu() {
    return this.contenu;
  }

  public ComposantDocument contenu(String contenu) {
    this.setContenu(contenu);
    return this;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  public TypeDocument getTypeDocument() {
    return this.typeDocument;
  }

  public void setTypeDocument(TypeDocument typeDocument) {
    this.typeDocument = typeDocument;
  }

  public ComposantDocument typeDocument(TypeDocument typeDocument) {
    this.setTypeDocument(typeDocument);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ComposantDocument)) {
      return false;
    }
    return id != null && id.equals(((ComposantDocument) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ComposantDocument{" +
            "id=" + getId() +
            ", intComposant='" + getIntComposant() + "'" +
            ", titreComposant='" + getTitreComposant() + "'" +
            ", contenu='" + getContenu() + "'" +
            "}";
    }
}
