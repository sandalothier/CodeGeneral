package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComposantDocumentDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(ComposantDocumentDTO.class);
    ComposantDocumentDTO composantDocumentDTO1 = new ComposantDocumentDTO();
    composantDocumentDTO1.setId("id1");
    ComposantDocumentDTO composantDocumentDTO2 = new ComposantDocumentDTO();
    assertThat(composantDocumentDTO1).isNotEqualTo(composantDocumentDTO2);
    composantDocumentDTO2.setId(composantDocumentDTO1.getId());
    assertThat(composantDocumentDTO1).isEqualTo(composantDocumentDTO2);
    composantDocumentDTO2.setId("id2");
    assertThat(composantDocumentDTO1).isNotEqualTo(composantDocumentDTO2);
    composantDocumentDTO1.setId(null);
    assertThat(composantDocumentDTO1).isNotEqualTo(composantDocumentDTO2);
  }
}
