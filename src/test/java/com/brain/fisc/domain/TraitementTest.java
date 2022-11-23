package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TraitementTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Traitement.class);
    Traitement traitement1 = new Traitement();
    traitement1.setId("id1");
    Traitement traitement2 = new Traitement();
    traitement2.setId(traitement1.getId());
    assertThat(traitement1).isEqualTo(traitement2);
    traitement2.setId("id2");
    assertThat(traitement1).isNotEqualTo(traitement2);
    traitement1.setId(null);
    assertThat(traitement1).isNotEqualTo(traitement2);
  }
}
