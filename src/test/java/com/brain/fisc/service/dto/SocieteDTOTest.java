package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocieteDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(SocieteDTO.class);
    SocieteDTO societeDTO1 = new SocieteDTO();
    societeDTO1.setId("id1");
    SocieteDTO societeDTO2 = new SocieteDTO();
    assertThat(societeDTO1).isNotEqualTo(societeDTO2);
    societeDTO2.setId(societeDTO1.getId());
    assertThat(societeDTO1).isEqualTo(societeDTO2);
    societeDTO2.setId("id2");
    assertThat(societeDTO1).isNotEqualTo(societeDTO2);
    societeDTO1.setId(null);
    assertThat(societeDTO1).isNotEqualTo(societeDTO2);
  }
}
