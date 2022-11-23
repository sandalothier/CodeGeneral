package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Permission entity.\n@author sanda\nLes motifs pour lesquels l'on prend légalement une permission\n- Décès\n- Natalité\n- Déménagement\n- Personnel\nCette table deduit le nombre de jour pris en congé si ceux-ci ne sont pas déductibles\nDéfinir la condition de déductibilité
 */
@Document(collection = "permission")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "permission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Permission implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 10)
  @Field("refpermission")
  private String refpermission;

  @Size(max = 25)
  @Field("int_permission")
  private String intPermission;

  @NotNull
  @Size(max = 25)
  @Field("motif")
  private String motif;

  @Field("deduct_perm")
  private Integer deductPerm;

  @DBRef
  @Field("dateOperation")
  @JsonIgnoreProperties(value = { "refpermissions", "refTraitements", "periode" }, allowSetters = true)
  private DateOperation dateOperation;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Permission id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefpermission() {
    return this.refpermission;
  }

  public Permission refpermission(String refpermission) {
    this.setRefpermission(refpermission);
    return this;
  }

  public void setRefpermission(String refpermission) {
    this.refpermission = refpermission;
  }

  public String getIntPermission() {
    return this.intPermission;
  }

  public Permission intPermission(String intPermission) {
    this.setIntPermission(intPermission);
    return this;
  }

  public void setIntPermission(String intPermission) {
    this.intPermission = intPermission;
  }

  public String getMotif() {
    return this.motif;
  }

  public Permission motif(String motif) {
    this.setMotif(motif);
    return this;
  }

  public void setMotif(String motif) {
    this.motif = motif;
  }

  public Integer getDeductPerm() {
    return this.deductPerm;
  }

  public Permission deductPerm(Integer deductPerm) {
    this.setDeductPerm(deductPerm);
    return this;
  }

  public void setDeductPerm(Integer deductPerm) {
    this.deductPerm = deductPerm;
  }

  public DateOperation getDateOperation() {
    return this.dateOperation;
  }

  public void setDateOperation(DateOperation dateOperation) {
    this.dateOperation = dateOperation;
  }

  public Permission dateOperation(DateOperation dateOperation) {
    this.setDateOperation(dateOperation);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Permission)) {
      return false;
    }
    return id != null && id.equals(((Permission) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Permission{" +
            "id=" + getId() +
            ", refpermission='" + getRefpermission() + "'" +
            ", intPermission='" + getIntPermission() + "'" +
            ", motif='" + getMotif() + "'" +
            ", deductPerm=" + getDeductPerm() +
            "}";
    }
}
