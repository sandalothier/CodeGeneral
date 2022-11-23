package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeDocumentDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(TypeDocumentDTO.class);
    TypeDocumentDTO typeDocumentDTO1 = new TypeDocumentDTO();
    typeDocumentDTO1.setId("id1");
    TypeDocumentDTO typeDocumentDTO2 = new TypeDocumentDTO();
    assertThat(typeDocumentDTO1).isNotEqualTo(typeDocumentDTO2);
    typeDocumentDTO2.setId(typeDocumentDTO1.getId());
    assertThat(typeDocumentDTO1).isEqualTo(typeDocumentDTO2);
    typeDocumentDTO2.setId("id2");
    assertThat(typeDocumentDTO1).isNotEqualTo(typeDocumentDTO2);
    typeDocumentDTO1.setId(null);
    assertThat(typeDocumentDTO1).isNotEqualTo(typeDocumentDTO2);
  }
}
