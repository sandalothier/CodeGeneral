package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * TypeDocument entity.\n@author sanda
 */
@Document(collection = "type_document")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "typedocument")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeDocument implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 50)
  @Field("int_type_doc")
  private String intTypeDoc;

  @DBRef
  @Field("intComposant")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "typeDocument" }, allowSetters = true)
  private Set<ComposantDocument> intComposants = new HashSet<>();

  @DBRef
  @Field("societe")
  @JsonIgnoreProperties(value = { "matricules", "intTypeDocs", "intSuccursales" }, allowSetters = true)
  private Societe societe;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public TypeDocument id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntTypeDoc() {
    return this.intTypeDoc;
  }

  public TypeDocument intTypeDoc(String intTypeDoc) {
    this.setIntTypeDoc(intTypeDoc);
    return this;
  }

  public void setIntTypeDoc(String intTypeDoc) {
    this.intTypeDoc = intTypeDoc;
  }

  public Set<ComposantDocument> getIntComposants() {
    return this.intComposants;
  }

  public void setIntComposants(Set<ComposantDocument> composantDocuments) {
    if (this.intComposants != null) {
      this.intComposants.forEach(i -> i.setTypeDocument(null));
    }
    if (composantDocuments != null) {
      composantDocuments.forEach(i -> i.setTypeDocument(this));
    }
    this.intComposants = composantDocuments;
  }

  public TypeDocument intComposants(Set<ComposantDocument> composantDocuments) {
    this.setIntComposants(composantDocuments);
    return this;
  }

  public TypeDocument addIntComposant(ComposantDocument composantDocument) {
    this.intComposants.add(composantDocument);
    composantDocument.setTypeDocument(this);
    return this;
  }

  public TypeDocument removeIntComposant(ComposantDocument composantDocument) {
    this.intComposants.remove(composantDocument);
    composantDocument.setTypeDocument(null);
    return this;
  }

  public Societe getSociete() {
    return this.societe;
  }

  public void setSociete(Societe societe) {
    this.societe = societe;
  }

  public TypeDocument societe(Societe societe) {
    this.setSociete(societe);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypeDocument)) {
      return false;
    }
    return id != null && id.equals(((TypeDocument) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TypeDocument{" +
            "id=" + getId() +
            ", intTypeDoc='" + getIntTypeDoc() + "'" +
            "}";
    }
}
