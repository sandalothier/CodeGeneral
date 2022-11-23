package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BulletinPaieTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(BulletinPaie.class);
    BulletinPaie bulletinPaie1 = new BulletinPaie();
    bulletinPaie1.setId("id1");
    BulletinPaie bulletinPaie2 = new BulletinPaie();
    bulletinPaie2.setId(bulletinPaie1.getId());
    assertThat(bulletinPaie1).isEqualTo(bulletinPaie2);
    bulletinPaie2.setId("id2");
    assertThat(bulletinPaie1).isNotEqualTo(bulletinPaie2);
    bulletinPaie1.setId(null);
    assertThat(bulletinPaie1).isNotEqualTo(bulletinPaie2);
  }
}
