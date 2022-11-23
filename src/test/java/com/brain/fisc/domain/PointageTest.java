package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PointageTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Pointage.class);
    Pointage pointage1 = new Pointage();
    pointage1.setId("id1");
    Pointage pointage2 = new Pointage();
    pointage2.setId(pointage1.getId());
    assertThat(pointage1).isEqualTo(pointage2);
    pointage2.setId("id2");
    assertThat(pointage1).isNotEqualTo(pointage2);
    pointage1.setId(null);
    assertThat(pointage1).isNotEqualTo(pointage2);
  }
}
