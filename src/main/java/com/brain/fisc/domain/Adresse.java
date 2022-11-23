package com.brain.fisc.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Adresse entity.\n@author sanda
 */
@Document(collection = "adresse")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "adresse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Adresse implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 8)
  @Field("cel")
  private String cel;

  @Size(max = 8)
  @Field("tel")
  private String tel;

  @Size(max = 25)
  @Field("region")
  private String region;

  @Size(max = 25)
  @Field("nom_rue")
  private String nomRue;

  @Size(max = 25)
  @Field("num_rue")
  private String numRue;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Adresse id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCel() {
    return this.cel;
  }

  public Adresse cel(String cel) {
    this.setCel(cel);
    return this;
  }

  public void setCel(String cel) {
    this.cel = cel;
  }

  public String getTel() {
    return this.tel;
  }

  public Adresse tel(String tel) {
    this.setTel(tel);
    return this;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getRegion() {
    return this.region;
  }

  public Adresse region(String region) {
    this.setRegion(region);
    return this;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getNomRue() {
    return this.nomRue;
  }

  public Adresse nomRue(String nomRue) {
    this.setNomRue(nomRue);
    return this;
  }

  public void setNomRue(String nomRue) {
    this.nomRue = nomRue;
  }

  public String getNumRue() {
    return this.numRue;
  }

  public Adresse numRue(String numRue) {
    this.setNumRue(numRue);
    return this;
  }

  public void setNumRue(String numRue) {
    this.numRue = numRue;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Adresse)) {
      return false;
    }
    return id != null && id.equals(((Adresse) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Adresse{" +
            "id=" + getId() +
            ", cel='" + getCel() + "'" +
            ", tel='" + getTel() + "'" +
            ", region='" + getRegion() + "'" +
            ", nomRue='" + getNomRue() + "'" +
            ", numRue='" + getNumRue() + "'" +
            "}";
    }
}
