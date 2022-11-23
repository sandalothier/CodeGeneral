package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiplomeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(DiplomeDTO.class);
    DiplomeDTO diplomeDTO1 = new DiplomeDTO();
    diplomeDTO1.setId("id1");
    DiplomeDTO diplomeDTO2 = new DiplomeDTO();
    assertThat(diplomeDTO1).isNotEqualTo(diplomeDTO2);
    diplomeDTO2.setId(diplomeDTO1.getId());
    assertThat(diplomeDTO1).isEqualTo(diplomeDTO2);
    diplomeDTO2.setId("id2");
    assertThat(diplomeDTO1).isNotEqualTo(diplomeDTO2);
    diplomeDTO1.setId(null);
    assertThat(diplomeDTO1).isNotEqualTo(diplomeDTO2);
  }
}
