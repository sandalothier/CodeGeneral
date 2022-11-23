package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeContratDeTravailTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(TypeContratDeTravail.class);
    TypeContratDeTravail typeContratDeTravail1 = new TypeContratDeTravail();
    typeContratDeTravail1.setId("id1");
    TypeContratDeTravail typeContratDeTravail2 = new TypeContratDeTravail();
    typeContratDeTravail2.setId(typeContratDeTravail1.getId());
    assertThat(typeContratDeTravail1).isEqualTo(typeContratDeTravail2);
    typeContratDeTravail2.setId("id2");
    assertThat(typeContratDeTravail1).isNotEqualTo(typeContratDeTravail2);
    typeContratDeTravail1.setId(null);
    assertThat(typeContratDeTravail1).isNotEqualTo(typeContratDeTravail2);
  }
}
