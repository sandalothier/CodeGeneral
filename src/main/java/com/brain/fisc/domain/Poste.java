package com.brain.fisc.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Poste entity.\n@author sanda
 */
@Document(collection = "poste")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "poste")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Poste implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 25)
  @Field("int_poste")
  private String intPoste;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Poste id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntPoste() {
    return this.intPoste;
  }

  public Poste intPoste(String intPoste) {
    this.setIntPoste(intPoste);
    return this;
  }

  public void setIntPoste(String intPoste) {
    this.intPoste = intPoste;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Poste)) {
      return false;
    }
    return id != null && id.equals(((Poste) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Poste{" +
            "id=" + getId() +
            ", intPoste='" + getIntPoste() + "'" +
            "}";
    }
}
