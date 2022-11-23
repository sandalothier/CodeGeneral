package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * DateOperation entity.\n@author sanda
 */
@Document(collection = "date_operation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dateoperation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DateOperation implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Field("datoperation")
  private LocalDate datoperation;

  @DBRef
  @Field("refpermission")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "dateOperation" }, allowSetters = true)
  private Set<Permission> refpermissions = new HashSet<>();

  @DBRef
  @Field("refTraitement")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "dateOperation" }, allowSetters = true)
  private Set<Traitement> refTraitements = new HashSet<>();

  @DBRef
  @Field("periode")
  @JsonIgnoreProperties(value = { "datoperations", "refBulletins", "refConges" }, allowSetters = true)
  private Periode periode;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public DateOperation id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getDatoperation() {
    return this.datoperation;
  }

  public DateOperation datoperation(LocalDate datoperation) {
    this.setDatoperation(datoperation);
    return this;
  }

  public void setDatoperation(LocalDate datoperation) {
    this.datoperation = datoperation;
  }

  public Set<Permission> getRefpermissions() {
    return this.refpermissions;
  }

  public void setRefpermissions(Set<Permission> permissions) {
    if (this.refpermissions != null) {
      this.refpermissions.forEach(i -> i.setDateOperation(null));
    }
    if (permissions != null) {
      permissions.forEach(i -> i.setDateOperation(this));
    }
    this.refpermissions = permissions;
  }

  public DateOperation refpermissions(Set<Permission> permissions) {
    this.setRefpermissions(permissions);
    return this;
  }

  public DateOperation addRefpermission(Permission permission) {
    this.refpermissions.add(permission);
    permission.setDateOperation(this);
    return this;
  }

  public DateOperation removeRefpermission(Permission permission) {
    this.refpermissions.remove(permission);
    permission.setDateOperation(null);
    return this;
  }

  public Set<Traitement> getRefTraitements() {
    return this.refTraitements;
  }

  public void setRefTraitements(Set<Traitement> traitements) {
    if (this.refTraitements != null) {
      this.refTraitements.forEach(i -> i.setDateOperation(null));
    }
    if (traitements != null) {
      traitements.forEach(i -> i.setDateOperation(this));
    }
    this.refTraitements = traitements;
  }

  public DateOperation refTraitements(Set<Traitement> traitements) {
    this.setRefTraitements(traitements);
    return this;
  }

  public DateOperation addRefTraitement(Traitement traitement) {
    this.refTraitements.add(traitement);
    traitement.setDateOperation(this);
    return this;
  }

  public DateOperation removeRefTraitement(Traitement traitement) {
    this.refTraitements.remove(traitement);
    traitement.setDateOperation(null);
    return this;
  }

  public Periode getPeriode() {
    return this.periode;
  }

  public void setPeriode(Periode periode) {
    this.periode = periode;
  }

  public DateOperation periode(Periode periode) {
    this.setPeriode(periode);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DateOperation)) {
      return false;
    }
    return id != null && id.equals(((DateOperation) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "DateOperation{" +
            "id=" + getId() +
            ", datoperation='" + getDatoperation() + "'" +
            "}";
    }
}
