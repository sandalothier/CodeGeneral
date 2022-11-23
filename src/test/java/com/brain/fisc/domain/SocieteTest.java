package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocieteTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Societe.class);
    Societe societe1 = new Societe();
    societe1.setId("id1");
    Societe societe2 = new Societe();
    societe2.setId(societe1.getId());
    assertThat(societe1).isEqualTo(societe2);
    societe2.setId("id2");
    assertThat(societe1).isNotEqualTo(societe2);
    societe1.setId(null);
    assertThat(societe1).isNotEqualTo(societe2);
  }
}
