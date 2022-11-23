package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DateOperationDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(DateOperationDTO.class);
    DateOperationDTO dateOperationDTO1 = new DateOperationDTO();
    dateOperationDTO1.setId("id1");
    DateOperationDTO dateOperationDTO2 = new DateOperationDTO();
    assertThat(dateOperationDTO1).isNotEqualTo(dateOperationDTO2);
    dateOperationDTO2.setId(dateOperationDTO1.getId());
    assertThat(dateOperationDTO1).isEqualTo(dateOperationDTO2);
    dateOperationDTO2.setId("id2");
    assertThat(dateOperationDTO1).isNotEqualTo(dateOperationDTO2);
    dateOperationDTO1.setId(null);
    assertThat(dateOperationDTO1).isNotEqualTo(dateOperationDTO2);
  }
}
