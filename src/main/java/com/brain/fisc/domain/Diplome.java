package com.brain.fisc.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Diplome entity.\n@author sanda
 */
@Document(collection = "diplome")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "diplome")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Diplome implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @NotNull
  @Size(max = 10)
  @Field("code_diplome")
  private String codeDiplome;

  @Size(max = 25)
  @Field("int_diplome")
  private String intDiplome;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public Diplome id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCodeDiplome() {
    return this.codeDiplome;
  }

  public Diplome codeDiplome(String codeDiplome) {
    this.setCodeDiplome(codeDiplome);
    return this;
  }

  public void setCodeDiplome(String codeDiplome) {
    this.codeDiplome = codeDiplome;
  }

  public String getIntDiplome() {
    return this.intDiplome;
  }

  public Diplome intDiplome(String intDiplome) {
    this.setIntDiplome(intDiplome);
    return this;
  }

  public void setIntDiplome(String intDiplome) {
    this.intDiplome = intDiplome;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Diplome)) {
      return false;
    }
    return id != null && id.equals(((Diplome) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Diplome{" +
            "id=" + getId() +
            ", codeDiplome='" + getCodeDiplome() + "'" +
            ", intDiplome='" + getIntDiplome() + "'" +
            "}";
    }
}
