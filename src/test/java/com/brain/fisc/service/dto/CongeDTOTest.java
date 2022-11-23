package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CongeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(CongeDTO.class);
    CongeDTO congeDTO1 = new CongeDTO();
    congeDTO1.setId("id1");
    CongeDTO congeDTO2 = new CongeDTO();
    assertThat(congeDTO1).isNotEqualTo(congeDTO2);
    congeDTO2.setId(congeDTO1.getId());
    assertThat(congeDTO1).isEqualTo(congeDTO2);
    congeDTO2.setId("id2");
    assertThat(congeDTO1).isNotEqualTo(congeDTO2);
    congeDTO1.setId(null);
    assertThat(congeDTO1).isNotEqualTo(congeDTO2);
  }
}
