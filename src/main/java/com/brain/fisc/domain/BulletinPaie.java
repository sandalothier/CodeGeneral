package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * BulletinPaie entity.\n@author sanda\nC'est le document qui est délivré au Agent justifiant leur dette vis à vis\nde l'administration
 */
@Document(collection = "bulletin_paie")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bulletinpaie")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BulletinPaie implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 10)
  @Field("ref_bulletin")
  private String refBulletin;

  @DBRef
  @Field("periode")
  @JsonIgnoreProperties(value = { "datoperations", "refBulletins", "refConges" }, allowSetters = true)
  private Periode periode;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public BulletinPaie id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefBulletin() {
    return this.refBulletin;
  }

  public BulletinPaie refBulletin(String refBulletin) {
    this.setRefBulletin(refBulletin);
    return this;
  }

  public void setRefBulletin(String refBulletin) {
    this.refBulletin = refBulletin;
  }

  public Periode getPeriode() {
    return this.periode;
  }

  public void setPeriode(Periode periode) {
    this.periode = periode;
  }

  public BulletinPaie periode(Periode periode) {
    this.setPeriode(periode);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BulletinPaie)) {
      return false;
    }
    return id != null && id.equals(((BulletinPaie) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BulletinPaie{" +
            "id=" + getId() +
            ", refBulletin='" + getRefBulletin() + "'" +
            "}";
    }
}
