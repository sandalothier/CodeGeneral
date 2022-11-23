package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * ContratEtablis entity.\n@author sanda
 */
@Document(collection = "contrat_etablis")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "contratetablis")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContratEtablis implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 10)
  @Field("ref_contrat")
  private String refContrat;

  @Field("date_etablissement")
  private LocalDate dateEtablissement;

  @DBRef
  @Field("intTypeContrat")
  private TypeContratDeTravail intTypeContrat;

  @DBRef
  @Field("intPeriode")
  private Periode intPeriode;

  @DBRef
  @Field("personnel")
  @JsonIgnoreProperties(value = { "codeDiplome", "cel", "intPoste", "refContrats", "societe", "pointage", "equipe" }, allowSetters = true)
  private Personnel personnel;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public ContratEtablis id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefContrat() {
    return this.refContrat;
  }

  public ContratEtablis refContrat(String refContrat) {
    this.setRefContrat(refContrat);
    return this;
  }

  public void setRefContrat(String refContrat) {
    this.refContrat = refContrat;
  }

  public LocalDate getDateEtablissement() {
    return this.dateEtablissement;
  }

  public ContratEtablis dateEtablissement(LocalDate dateEtablissement) {
    this.setDateEtablissement(dateEtablissement);
    return this;
  }

  public void setDateEtablissement(LocalDate dateEtablissement) {
    this.dateEtablissement = dateEtablissement;
  }

  public TypeContratDeTravail getIntTypeContrat() {
    return this.intTypeContrat;
  }

  public void setIntTypeContrat(TypeContratDeTravail typeContratDeTravail) {
    this.intTypeContrat = typeContratDeTravail;
  }

  public ContratEtablis intTypeContrat(TypeContratDeTravail typeContratDeTravail) {
    this.setIntTypeContrat(typeContratDeTravail);
    return this;
  }

  public Periode getIntPeriode() {
    return this.intPeriode;
  }

  public void setIntPeriode(Periode periode) {
    this.intPeriode = periode;
  }

  public ContratEtablis intPeriode(Periode periode) {
    this.setIntPeriode(periode);
    return this;
  }

  public Personnel getPersonnel() {
    return this.personnel;
  }

  public void setPersonnel(Personnel personnel) {
    this.personnel = personnel;
  }

  public ContratEtablis personnel(Personnel personnel) {
    this.setPersonnel(personnel);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ContratEtablis)) {
      return false;
    }
    return id != null && id.equals(((ContratEtablis) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ContratEtablis{" +
            "id=" + getId() +
            ", refContrat='" + getRefContrat() + "'" +
            ", dateEtablissement='" + getDateEtablissement() + "'" +
            "}";
    }
}
