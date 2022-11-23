package com.brain.fisc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Societe entity.\n@author sanda
 */
@Document(collection = "societe")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "societe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Societe implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 25)
  @Field("int_societe")
  private String intSociete;

  @Size(max = 25)
  @Field("sigle")
  private String sigle;

  @NotNull
  @Size(max = 25)
  @Field("logo")
  private String logo;

  @Size(max = 25)
  @Field("siege")
  private String siege;

  @DBRef
  @Field("matricule")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "codeDiplome", "cel", "intPoste", "refContrats", "societe", "pointage", "equipe" }, allowSetters = true)
  private Set<Personnel> matricules = new HashSet<>();

  @DBRef
  @Field("intTypeDoc")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "intComposants", "societe" }, allowSetters = true)
  private Set<TypeDocument> intTypeDocs = new HashSet<>();

  @DBRef
  @Field("intSuccursale")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "societe" }, allowSetters = true)
  private Set<Succursale> intSuccursales = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Societe id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntSociete() {
    return this.intSociete;
  }

  public Societe intSociete(String intSociete) {
    this.setIntSociete(intSociete);
    return this;
  }

  public void setIntSociete(String intSociete) {
    this.intSociete = intSociete;
  }

  public String getSigle() {
    return this.sigle;
  }

  public Societe sigle(String sigle) {
    this.setSigle(sigle);
    return this;
  }

  public void setSigle(String sigle) {
    this.sigle = sigle;
  }

  public String getLogo() {
    return this.logo;
  }

  public Societe logo(String logo) {
    this.setLogo(logo);
    return this;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getSiege() {
    return this.siege;
  }

  public Societe siege(String siege) {
    this.setSiege(siege);
    return this;
  }

  public void setSiege(String siege) {
    this.siege = siege;
  }

  public Set<Personnel> getMatricules() {
    return this.matricules;
  }

  public void setMatricules(Set<Personnel> personnel) {
    if (this.matricules != null) {
      this.matricules.forEach(i -> i.setSociete(null));
    }
    if (personnel != null) {
      personnel.forEach(i -> i.setSociete(this));
    }
    this.matricules = personnel;
  }

  public Societe matricules(Set<Personnel> personnel) {
    this.setMatricules(personnel);
    return this;
  }

  public Societe addMatricule(Personnel personnel) {
    this.matricules.add(personnel);
    personnel.setSociete(this);
    return this;
  }

  public Societe removeMatricule(Personnel personnel) {
    this.matricules.remove(personnel);
    personnel.setSociete(null);
    return this;
  }

  public Set<TypeDocument> getIntTypeDocs() {
    return this.intTypeDocs;
  }

  public void setIntTypeDocs(Set<TypeDocument> typeDocuments) {
    if (this.intTypeDocs != null) {
      this.intTypeDocs.forEach(i -> i.setSociete(null));
    }
    if (typeDocuments != null) {
      typeDocuments.forEach(i -> i.setSociete(this));
    }
    this.intTypeDocs = typeDocuments;
  }

  public Societe intTypeDocs(Set<TypeDocument> typeDocuments) {
    this.setIntTypeDocs(typeDocuments);
    return this;
  }

  public Societe addIntTypeDoc(TypeDocument typeDocument) {
    this.intTypeDocs.add(typeDocument);
    typeDocument.setSociete(this);
    return this;
  }

  public Societe removeIntTypeDoc(TypeDocument typeDocument) {
    this.intTypeDocs.remove(typeDocument);
    typeDocument.setSociete(null);
    return this;
  }

  public Set<Succursale> getIntSuccursales() {
    return this.intSuccursales;
  }

  public void setIntSuccursales(Set<Succursale> succursales) {
    if (this.intSuccursales != null) {
      this.intSuccursales.forEach(i -> i.setSociete(null));
    }
    if (succursales != null) {
      succursales.forEach(i -> i.setSociete(this));
    }
    this.intSuccursales = succursales;
  }

  public Societe intSuccursales(Set<Succursale> succursales) {
    this.setIntSuccursales(succursales);
    return this;
  }

  public Societe addIntSuccursale(Succursale succursale) {
    this.intSuccursales.add(succursale);
    succursale.setSociete(this);
    return this;
  }

  public Societe removeIntSuccursale(Succursale succursale) {
    this.intSuccursales.remove(succursale);
    succursale.setSociete(null);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Societe)) {
      return false;
    }
    return id != null && id.equals(((Societe) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Societe{" +
            "id=" + getId() +
            ", intSociete='" + getIntSociete() + "'" +
            ", sigle='" + getSigle() + "'" +
            ", logo='" + getLogo() + "'" +
            ", siege='" + getSiege() + "'" +
            "}";
    }
}
