package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipeTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Equipe.class);
    Equipe equipe1 = new Equipe();
    equipe1.setId("id1");
    Equipe equipe2 = new Equipe();
    equipe2.setId(equipe1.getId());
    assertThat(equipe1).isEqualTo(equipe2);
    equipe2.setId("id2");
    assertThat(equipe1).isNotEqualTo(equipe2);
    equipe1.setId(null);
    assertThat(equipe1).isNotEqualTo(equipe2);
  }
}
