package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Periode entity.\n@author sanda
 */
@Document(collection = "periode")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "periode")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Periode implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 25)
  @Field("int_periode")
  private String intPeriode;

  @Field("debut")
  private LocalDate debut;

  @Field("fin")
  private LocalDate fin;

  @DBRef
  @Field("datoperation")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "refpermissions", "refTraitements", "periode" }, allowSetters = true)
  private Set<DateOperation> datoperations = new HashSet<>();

  @DBRef
  @Field("refBulletin")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "periode" }, allowSetters = true)
  private Set<BulletinPaie> refBulletins = new HashSet<>();

  @DBRef
  @Field("refConge")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "periode" }, allowSetters = true)
  private Set<Conge> refConges = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Periode id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntPeriode() {
    return this.intPeriode;
  }

  public Periode intPeriode(String intPeriode) {
    this.setIntPeriode(intPeriode);
    return this;
  }

  public void setIntPeriode(String intPeriode) {
    this.intPeriode = intPeriode;
  }

  public LocalDate getDebut() {
    return this.debut;
  }

  public Periode debut(LocalDate debut) {
    this.setDebut(debut);
    return this;
  }

  public void setDebut(LocalDate debut) {
    this.debut = debut;
  }

  public LocalDate getFin() {
    return this.fin;
  }

  public Periode fin(LocalDate fin) {
    this.setFin(fin);
    return this;
  }

  public void setFin(LocalDate fin) {
    this.fin = fin;
  }

  public Set<DateOperation> getDatoperations() {
    return this.datoperations;
  }

  public void setDatoperations(Set<DateOperation> dateOperations) {
    if (this.datoperations != null) {
      this.datoperations.forEach(i -> i.setPeriode(null));
    }
    if (dateOperations != null) {
      dateOperations.forEach(i -> i.setPeriode(this));
    }
    this.datoperations = dateOperations;
  }

  public Periode datoperations(Set<DateOperation> dateOperations) {
    this.setDatoperations(dateOperations);
    return this;
  }

  public Periode addDatoperation(DateOperation dateOperation) {
    this.datoperations.add(dateOperation);
    dateOperation.setPeriode(this);
    return this;
  }

  public Periode removeDatoperation(DateOperation dateOperation) {
    this.datoperations.remove(dateOperation);
    dateOperation.setPeriode(null);
    return this;
  }

  public Set<BulletinPaie> getRefBulletins() {
    return this.refBulletins;
  }

  public void setRefBulletins(Set<BulletinPaie> bulletinPaies) {
    if (this.refBulletins != null) {
      this.refBulletins.forEach(i -> i.setPeriode(null));
    }
    if (bulletinPaies != null) {
      bulletinPaies.forEach(i -> i.setPeriode(this));
    }
    this.refBulletins = bulletinPaies;
  }

  public Periode refBulletins(Set<BulletinPaie> bulletinPaies) {
    this.setRefBulletins(bulletinPaies);
    return this;
  }

  public Periode addRefBulletin(BulletinPaie bulletinPaie) {
    this.refBulletins.add(bulletinPaie);
    bulletinPaie.setPeriode(this);
    return this;
  }

  public Periode removeRefBulletin(BulletinPaie bulletinPaie) {
    this.refBulletins.remove(bulletinPaie);
    bulletinPaie.setPeriode(null);
    return this;
  }

  public Set<Conge> getRefConges() {
    return this.refConges;
  }

  public void setRefConges(Set<Conge> conges) {
    if (this.refConges != null) {
      this.refConges.forEach(i -> i.setPeriode(null));
    }
    if (conges != null) {
      conges.forEach(i -> i.setPeriode(this));
    }
    this.refConges = conges;
  }

  public Periode refConges(Set<Conge> conges) {
    this.setRefConges(conges);
    return this;
  }

  public Periode addRefConge(Conge conge) {
    this.refConges.add(conge);
    conge.setPeriode(this);
    return this;
  }

  public Periode removeRefConge(Conge conge) {
    this.refConges.remove(conge);
    conge.setPeriode(null);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Periode)) {
      return false;
    }
    return id != null && id.equals(((Periode) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Periode{" +
            "id=" + getId() +
            ", intPeriode='" + getIntPeriode() + "'" +
            ", debut='" + getDebut() + "'" +
            ", fin='" + getFin() + "'" +
            "}";
    }
}
