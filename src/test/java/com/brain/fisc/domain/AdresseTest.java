package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdresseTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Adresse.class);
    Adresse adresse1 = new Adresse();
    adresse1.setId("id1");
    Adresse adresse2 = new Adresse();
    adresse2.setId(adresse1.getId());
    assertThat(adresse1).isEqualTo(adresse2);
    adresse2.setId("id2");
    assertThat(adresse1).isNotEqualTo(adresse2);
    adresse1.setId(null);
    assertThat(adresse1).isNotEqualTo(adresse2);
  }
}
