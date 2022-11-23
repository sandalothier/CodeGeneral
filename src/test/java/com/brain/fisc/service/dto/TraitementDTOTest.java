package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TraitementDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(TraitementDTO.class);
    TraitementDTO traitementDTO1 = new TraitementDTO();
    traitementDTO1.setId("id1");
    TraitementDTO traitementDTO2 = new TraitementDTO();
    assertThat(traitementDTO1).isNotEqualTo(traitementDTO2);
    traitementDTO2.setId(traitementDTO1.getId());
    assertThat(traitementDTO1).isEqualTo(traitementDTO2);
    traitementDTO2.setId("id2");
    assertThat(traitementDTO1).isNotEqualTo(traitementDTO2);
    traitementDTO1.setId(null);
    assertThat(traitementDTO1).isNotEqualTo(traitementDTO2);
  }
}
