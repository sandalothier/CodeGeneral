package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Conge entity.\n@author sanda\nCette table est liée aux permissions où ils déduisent ceux qui le sont.\nElle se constitue suivant des critères bien précis:\n- Type de contrat\n- Durée dans la fonction\n- Déduction\n- Augmentation suivantun cycle
 */
@Document(collection = "conge")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "conge")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Conge implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 10)
  @Field("ref_conge")
  private String refConge;

  @DBRef
  @Field("periode")
  @JsonIgnoreProperties(value = { "datoperations", "refBulletins", "refConges" }, allowSetters = true)
  private Periode periode;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Conge id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefConge() {
    return this.refConge;
  }

  public Conge refConge(String refConge) {
    this.setRefConge(refConge);
    return this;
  }

  public void setRefConge(String refConge) {
    this.refConge = refConge;
  }

  public Periode getPeriode() {
    return this.periode;
  }

  public void setPeriode(Periode periode) {
    this.periode = periode;
  }

  public Conge periode(Periode periode) {
    this.setPeriode(periode);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Conge)) {
      return false;
    }
    return id != null && id.equals(((Conge) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Conge{" +
            "id=" + getId() +
            ", refConge='" + getRefConge() + "'" +
            "}";
    }
}
