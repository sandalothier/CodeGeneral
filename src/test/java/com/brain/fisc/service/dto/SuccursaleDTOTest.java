package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SuccursaleDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(SuccursaleDTO.class);
    SuccursaleDTO succursaleDTO1 = new SuccursaleDTO();
    succursaleDTO1.setId("id1");
    SuccursaleDTO succursaleDTO2 = new SuccursaleDTO();
    assertThat(succursaleDTO1).isNotEqualTo(succursaleDTO2);
    succursaleDTO2.setId(succursaleDTO1.getId());
    assertThat(succursaleDTO1).isEqualTo(succursaleDTO2);
    succursaleDTO2.setId("id2");
    assertThat(succursaleDTO1).isNotEqualTo(succursaleDTO2);
    succursaleDTO1.setId(null);
    assertThat(succursaleDTO1).isNotEqualTo(succursaleDTO2);
  }
}
