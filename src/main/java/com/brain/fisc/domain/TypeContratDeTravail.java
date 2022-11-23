package com.brain.fisc.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * TypeContratDeTravail entity.\n@author sanda
 */
@Document(collection = "type_contrat_de_travail")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "typecontratdetravail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeContratDeTravail implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 25)
  @Field("int_type_contrat")
  private String intTypeContrat;

  @NotNull
  @Size(max = 50)
  @Field("description")
  private String description;

  @NotNull
  @Field("duree_max")
  private Integer dureeMax;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public TypeContratDeTravail id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntTypeContrat() {
    return this.intTypeContrat;
  }

  public TypeContratDeTravail intTypeContrat(String intTypeContrat) {
    this.setIntTypeContrat(intTypeContrat);
    return this;
  }

  public void setIntTypeContrat(String intTypeContrat) {
    this.intTypeContrat = intTypeContrat;
  }

  public String getDescription() {
    return this.description;
  }

  public TypeContratDeTravail description(String description) {
    this.setDescription(description);
    return this;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDureeMax() {
    return this.dureeMax;
  }

  public TypeContratDeTravail dureeMax(Integer dureeMax) {
    this.setDureeMax(dureeMax);
    return this;
  }

  public void setDureeMax(Integer dureeMax) {
    this.dureeMax = dureeMax;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TypeContratDeTravail)) {
      return false;
    }
    return id != null && id.equals(((TypeContratDeTravail) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TypeContratDeTravail{" +
            "id=" + getId() +
            ", intTypeContrat='" + getIntTypeContrat() + "'" +
            ", description='" + getDescription() + "'" +
            ", dureeMax=" + getDureeMax() +
            "}";
    }
}
