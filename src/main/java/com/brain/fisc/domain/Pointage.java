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
 * Pointage entity.\n@author sanda\nCette table est en lien avec\nla table emploi du temps et le calendrier
 */
@Document(collection = "pointage")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "pointage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pointage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Field("heur_arrivee")
  private LocalDate heurArrivee;

  @NotNull
  @Field("heur_depart")
  private LocalDate heurDepart;

  @DBRef
  @Field("matricule")
  @org.springframework.data.annotation.Transient
  @JsonIgnoreProperties(value = { "codeDiplome", "cel", "intPoste", "refContrats", "societe", "pointage", "equipe" }, allowSetters = true)
  private Set<Personnel> matricules = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Pointage id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getHeurArrivee() {
    return this.heurArrivee;
  }

  public Pointage heurArrivee(LocalDate heurArrivee) {
    this.setHeurArrivee(heurArrivee);
    return this;
  }

  public void setHeurArrivee(LocalDate heurArrivee) {
    this.heurArrivee = heurArrivee;
  }

  public LocalDate getHeurDepart() {
    return this.heurDepart;
  }

  public Pointage heurDepart(LocalDate heurDepart) {
    this.setHeurDepart(heurDepart);
    return this;
  }

  public void setHeurDepart(LocalDate heurDepart) {
    this.heurDepart = heurDepart;
  }

  public Set<Personnel> getMatricules() {
    return this.matricules;
  }

  public void setMatricules(Set<Personnel> personnel) {
    if (this.matricules != null) {
      this.matricules.forEach(i -> i.setPointage(null));
    }
    if (personnel != null) {
      personnel.forEach(i -> i.setPointage(this));
    }
    this.matricules = personnel;
  }

  public Pointage matricules(Set<Personnel> personnel) {
    this.setMatricules(personnel);
    return this;
  }

  public Pointage addMatricule(Personnel personnel) {
    this.matricules.add(personnel);
    personnel.setPointage(this);
    return this;
  }

  public Pointage removeMatricule(Personnel personnel) {
    this.matricules.remove(personnel);
    personnel.setPointage(null);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Pointage)) {
      return false;
    }
    return id != null && id.equals(((Pointage) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Pointage{" +
            "id=" + getId() +
            ", heurArrivee='" + getHeurArrivee() + "'" +
            ", heurDepart='" + getHeurDepart() + "'" +
            "}";
    }
}
