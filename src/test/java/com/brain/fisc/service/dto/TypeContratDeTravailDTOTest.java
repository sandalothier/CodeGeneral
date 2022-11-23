package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeContratDeTravailDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(TypeContratDeTravailDTO.class);
    TypeContratDeTravailDTO typeContratDeTravailDTO1 = new TypeContratDeTravailDTO();
    typeContratDeTravailDTO1.setId("id1");
    TypeContratDeTravailDTO typeContratDeTravailDTO2 = new TypeContratDeTravailDTO();
    assertThat(typeContratDeTravailDTO1).isNotEqualTo(typeContratDeTravailDTO2);
    typeContratDeTravailDTO2.setId(typeContratDeTravailDTO1.getId());
    assertThat(typeContratDeTravailDTO1).isEqualTo(typeContratDeTravailDTO2);
    typeContratDeTravailDTO2.setId("id2");
    assertThat(typeContratDeTravailDTO1).isNotEqualTo(typeContratDeTravailDTO2);
    typeContratDeTravailDTO1.setId(null);
    assertThat(typeContratDeTravailDTO1).isNotEqualTo(typeContratDeTravailDTO2);
  }
}
