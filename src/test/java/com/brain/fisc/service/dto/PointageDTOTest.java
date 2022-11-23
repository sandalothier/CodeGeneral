package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PointageDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PointageDTO.class);
    PointageDTO pointageDTO1 = new PointageDTO();
    pointageDTO1.setId("id1");
    PointageDTO pointageDTO2 = new PointageDTO();
    assertThat(pointageDTO1).isNotEqualTo(pointageDTO2);
    pointageDTO2.setId(pointageDTO1.getId());
    assertThat(pointageDTO1).isEqualTo(pointageDTO2);
    pointageDTO2.setId("id2");
    assertThat(pointageDTO1).isNotEqualTo(pointageDTO2);
    pointageDTO1.setId(null);
    assertThat(pointageDTO1).isNotEqualTo(pointageDTO2);
  }
}
