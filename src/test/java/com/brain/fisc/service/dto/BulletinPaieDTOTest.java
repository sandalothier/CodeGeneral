package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BulletinPaieDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(BulletinPaieDTO.class);
    BulletinPaieDTO bulletinPaieDTO1 = new BulletinPaieDTO();
    bulletinPaieDTO1.setId("id1");
    BulletinPaieDTO bulletinPaieDTO2 = new BulletinPaieDTO();
    assertThat(bulletinPaieDTO1).isNotEqualTo(bulletinPaieDTO2);
    bulletinPaieDTO2.setId(bulletinPaieDTO1.getId());
    assertThat(bulletinPaieDTO1).isEqualTo(bulletinPaieDTO2);
    bulletinPaieDTO2.setId("id2");
    assertThat(bulletinPaieDTO1).isNotEqualTo(bulletinPaieDTO2);
    bulletinPaieDTO1.setId(null);
    assertThat(bulletinPaieDTO1).isNotEqualTo(bulletinPaieDTO2);
  }
}
