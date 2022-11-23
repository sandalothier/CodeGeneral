package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PosteTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Poste.class);
    Poste poste1 = new Poste();
    poste1.setId("id1");
    Poste poste2 = new Poste();
    poste2.setId(poste1.getId());
    assertThat(poste1).isEqualTo(poste2);
    poste2.setId("id2");
    assertThat(poste1).isNotEqualTo(poste2);
    poste1.setId(null);
    assertThat(poste1).isNotEqualTo(poste2);
  }
}
