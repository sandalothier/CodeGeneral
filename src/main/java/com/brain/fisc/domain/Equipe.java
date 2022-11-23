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
 * Equipe entity.\n@author sanda\nCette table est en relation avec celle acteur pour la constitution\ndes équipes.
 */
@Document(collection = "equipe")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "equipe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipe implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Size(max = 10)
  @Field("ref_equipe")
  private String refEquipe;

  @DBRef
  @Field("matricule")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "codeDiplome", "cel", "intPoste", "refContrats", "societe", "pointage", "equipe" }, allowSetters = true)
  private Set<Personnel> matricules = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Equipe id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRefEquipe() {
    return this.refEquipe;
  }

  public Equipe refEquipe(String refEquipe) {
    this.setRefEquipe(refEquipe);
    return this;
  }

  public void setRefEquipe(String refEquipe) {
    this.refEquipe = refEquipe;
  }

  public Set<Personnel> getMatricules() {
    return this.matricules;
  }

  public void setMatricules(Set<Personnel> personnel) {
    if (this.matricules != null) {
      this.matricules.forEach(i -> i.setEquipe(null));
    }
    if (personnel != null) {
      personnel.forEach(i -> i.setEquipe(this));
    }
    this.matricules = personnel;
  }

  public Equipe matricules(Set<Personnel> personnel) {
    this.setMatricules(personnel);
    return this;
  }

  public Equipe addMatricule(Personnel personnel) {
    this.matricules.add(personnel);
    personnel.setEquipe(this);
    return this;
  }

  public Equipe removeMatricule(Personnel personnel) {
    this.matricules.remove(personnel);
    personnel.setEquipe(null);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Equipe)) {
      return false;
    }
    return id != null && id.equals(((Equipe) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Equipe{" +
            "id=" + getId() +
            ", refEquipe='" + getRefEquipe() + "'" +
            "}";
    }
}
