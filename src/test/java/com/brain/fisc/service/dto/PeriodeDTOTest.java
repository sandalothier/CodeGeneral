package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PeriodeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PeriodeDTO.class);
    PeriodeDTO periodeDTO1 = new PeriodeDTO();
    periodeDTO1.setId("id1");
    PeriodeDTO periodeDTO2 = new PeriodeDTO();
    assertThat(periodeDTO1).isNotEqualTo(periodeDTO2);
    periodeDTO2.setId(periodeDTO1.getId());
    assertThat(periodeDTO1).isEqualTo(periodeDTO2);
    periodeDTO2.setId("id2");
    assertThat(periodeDTO1).isNotEqualTo(periodeDTO2);
    periodeDTO1.setId(null);
    assertThat(periodeDTO1).isNotEqualTo(periodeDTO2);
  }
}
