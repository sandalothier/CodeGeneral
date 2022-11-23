package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonnelDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PersonnelDTO.class);
    PersonnelDTO personnelDTO1 = new PersonnelDTO();
    personnelDTO1.setId("id1");
    PersonnelDTO personnelDTO2 = new PersonnelDTO();
    assertThat(personnelDTO1).isNotEqualTo(personnelDTO2);
    personnelDTO2.setId(personnelDTO1.getId());
    assertThat(personnelDTO1).isEqualTo(personnelDTO2);
    personnelDTO2.setId("id2");
    assertThat(personnelDTO1).isNotEqualTo(personnelDTO2);
    personnelDTO1.setId(null);
    assertThat(personnelDTO1).isNotEqualTo(personnelDTO2);
  }
}
