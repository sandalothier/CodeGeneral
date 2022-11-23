package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Succursale entity.\n@author sanda
 */
@Document(collection = "succursale")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "succursale")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Succursale implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 25)
  @Field("int_succursale")
  private String intSuccursale;

  @DBRef
  @Field("societe")
  @JsonIgnoreProperties(value = { "matricules", "intTypeDocs", "intSuccursales" }, allowSetters = true)
  private Societe societe;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Succursale id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntSuccursale() {
    return this.intSuccursale;
  }

  public Succursale intSuccursale(String intSuccursale) {
    this.setIntSuccursale(intSuccursale);
    return this;
  }

  public void setIntSuccursale(String intSuccursale) {
    this.intSuccursale = intSuccursale;
  }

  public Societe getSociete() {
    return this.societe;
  }

  public void setSociete(Societe societe) {
    this.societe = societe;
  }

  public Succursale societe(Societe societe) {
    this.setSociete(societe);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Succursale)) {
      return false;
    }
    return id != null && id.equals(((Succursale) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Succursale{" +
            "id=" + getId() +
            ", intSuccursale='" + getIntSuccursale() + "'" +
            "}";
    }
}
