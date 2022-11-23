package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(EquipeDTO.class);
    EquipeDTO equipeDTO1 = new EquipeDTO();
    equipeDTO1.setId("id1");
    EquipeDTO equipeDTO2 = new EquipeDTO();
    assertThat(equipeDTO1).isNotEqualTo(equipeDTO2);
    equipeDTO2.setId(equipeDTO1.getId());
    assertThat(equipeDTO1).isEqualTo(equipeDTO2);
    equipeDTO2.setId("id2");
    assertThat(equipeDTO1).isNotEqualTo(equipeDTO2);
    equipeDTO1.setId(null);
    assertThat(equipeDTO1).isNotEqualTo(equipeDTO2);
  }
}
