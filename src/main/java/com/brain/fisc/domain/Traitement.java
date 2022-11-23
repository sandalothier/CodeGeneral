package com.brain.fisc.domain;

import com.brain.fisc.domain.enumeration.TypeTraitement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Traitement entity.\n@author sanda\nCe sont les sommmes que le Agent prend auprès de l'administration\n
 */
@Document(collection = "traitement")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "traitement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Traitement implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 10)
  @Field("ref_traitement")
  private String refTraitement;

  @Field("type_traitement")
  private TypeTraitement typeTraitement;

  @Field("montant")
  private Double montant;

  @DBRef
  @Field("dateOperation")
  @JsonIgnoreProperties(value = { "refpermissions", "refTraitements", "periode" }, allowSetters = true)
  private DateOperation dateOperation;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Traitement id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefTraitement() {
    return this.refTraitement;
  }

  public Traitement refTraitement(String refTraitement) {
    this.setRefTraitement(refTraitement);
    return this;
  }

  public void setRefTraitement(String refTraitement) {
    this.refTraitement = refTraitement;
  }

  public TypeTraitement getTypeTraitement() {
    return this.typeTraitement;
  }

  public Traitement typeTraitement(TypeTraitement typeTraitement) {
    this.setTypeTraitement(typeTraitement);
    return this;
  }

  public void setTypeTraitement(TypeTraitement typeTraitement) {
    this.typeTraitement = typeTraitement;
  }

  public Double getMontant() {
    return this.montant;
  }

  public Traitement montant(Double montant) {
    this.setMontant(montant);
    return this;
  }

  public void setMontant(Double montant) {
    this.montant = montant;
  }

  public DateOperation getDateOperation() {
    return this.dateOperation;
  }

  public void setDateOperation(DateOperation dateOperation) {
    this.dateOperation = dateOperation;
  }

  public Traitement dateOperation(DateOperation dateOperation) {
    this.setDateOperation(dateOperation);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Traitement)) {
      return false;
    }
    return id != null && id.equals(((Traitement) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Traitement{" +
            "id=" + getId() +
            ", refTraitement='" + getRefTraitement() + "'" +
            ", typeTraitement='" + getTypeTraitement() + "'" +
            ", montant=" + getMontant() +
            "}";
    }
}
