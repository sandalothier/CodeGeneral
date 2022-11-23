package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiplomeTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Diplome.class);
    Diplome diplome1 = new Diplome();
    diplome1.setId("id1");
    Diplome diplome2 = new Diplome();
    diplome2.setId(diplome1.getId());
    assertThat(diplome1).isEqualTo(diplome2);
    diplome2.setId("id2");
    assertThat(diplome1).isNotEqualTo(diplome2);
    diplome1.setId(null);
    assertThat(diplome1).isNotEqualTo(diplome2);
  }
}
