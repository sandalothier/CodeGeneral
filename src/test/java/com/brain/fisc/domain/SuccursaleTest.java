package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SuccursaleTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Succursale.class);
    Succursale succursale1 = new Succursale();
    succursale1.setId("id1");
    Succursale succursale2 = new Succursale();
    succursale2.setId(succursale1.getId());
    assertThat(succursale1).isEqualTo(succursale2);
    succursale2.setId("id2");
    assertThat(succursale1).isNotEqualTo(succursale2);
    succursale1.setId(null);
    assertThat(succursale1).isNotEqualTo(succursale2);
  }
}
