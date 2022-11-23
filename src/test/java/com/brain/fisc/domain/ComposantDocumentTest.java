package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComposantDocumentTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(ComposantDocument.class);
    ComposantDocument composantDocument1 = new ComposantDocument();
    composantDocument1.setId("id1");
    ComposantDocument composantDocument2 = new ComposantDocument();
    composantDocument2.setId(composantDocument1.getId());
    assertThat(composantDocument1).isEqualTo(composantDocument2);
    composantDocument2.setId("id2");
    assertThat(composantDocument1).isNotEqualTo(composantDocument2);
    composantDocument1.setId(null);
    assertThat(composantDocument1).isNotEqualTo(composantDocument2);
  }
}
